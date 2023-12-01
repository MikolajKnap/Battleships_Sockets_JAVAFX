package server;

import com.example.shipsgamegui.ClientGUISettings;
import com.example.shipsgamegui.SerializableArrayList;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class ClientHandler implements Runnable {

    public static Set<ClientHandler> clientHandlers = new HashSet<>();
    private static ArrayList<String> usernamesList = new ArrayList<>();
    private Socket socket;
    private Server server;
    private Room currentRoom;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String username;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;


    public ClientHandler(Socket socket, Server server) {
        try {
            this.socket = socket;
            this.server = server;

            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());


            clientHandlers.add(this);

        } catch (IOException e) {
            e.printStackTrace();
            //TODO : handle
        }
    }

    @Override
    public void run() {
        try {
            while(username == null){
                username = bufferedReader.readLine();
                if(!usernamesList.contains(username)){
                    sendMessage("ACK");
                    usernamesList.add(username);
                }
                else{
                    username = null;
                    sendMessage("NACK");
                }
            }



            String messageFromClient;
            label:
            while (socket.isConnected()) {
                messageFromClient = bufferedReader.readLine();
                switch (messageFromClient) {
                    case "1" -> {
                        String roomName = String.format(this.username + "'s Room");
                        currentRoom = server.createRoom(roomName, this);
                        break label;
                    }
                    case "2" -> {
                        String roomName = bufferedReader.readLine();
                        Room roomToJoin = server.getRooms().stream()
                                .filter(room -> room.getRoomName().equals(roomName))
                                .findFirst()
                                .orElse(null);

                        if (roomToJoin != null) {
                            if (roomToJoin.getPlayer2() == null) {
                                roomToJoin.addPlayer2(this);
                                currentRoom = roomToJoin;
                                currentRoom.getLatchRoomPhase().countDown();
                                sendMessage("ACK");
                                break label;
                            } else {
                                sendMessage("FULL_ROOM");
                            }
                        } else {
                            sendMessage("ERROR");
                        }
                    }
                    case "3" -> {
                        ArrayList<ArrayList<String>> arrayToSend = new ArrayList<>();
                        arrayToSend.add(server.getRoomsString());
                        SerializableArrayList serializableArrayToSend = new SerializableArrayList(arrayToSend);
                        objectOutputStream.writeObject(serializableArrayToSend);
                    }
                    default -> {
                        sendMessage("ERROR");
                    }
                }
            }

            latchWaiter(currentRoom.getLatchRoomPhase(), 0);
            if(currentRoom.getHost() == this){
                sendMessage("PLACE_PHASE");
                System.out.println("POSZLO");
            }

            gameBoardsSetter();

            latchWaiter(currentRoom.getLatchPlacingPhase(),0);

            sendMessage("GAME_PHASE");
            if(currentRoom.getHost() == this){
                sendMessage("play");
            }
            else if(currentRoom.getPlayer2() == this){
                sendMessage("wait");
            }
            while (!currentRoom.isGameOver() && socket.isConnected()) {
                Thread.sleep(1000);
                if (currentRoom.getWhoToPlay() == this) {
                    String position = bufferedReader.readLine(); //a1
                    try {
                        String processedShot = processShot(position, currentRoom.getArrayBasedOnPlayerWhoDoesntPlay());
                        switch (processedShot) {
                            case "SHOT" -> {
                                System.out.println("You have shot opponent's ship!");
                                sendMessage("SHOT");
                                sendMessageToClient("OPPONENT_SHOT",currentRoom.getPlayerWhoDoesntPlay());
                                sendMessageToClient(position,currentRoom.getPlayerWhoDoesntPlay());
                            }
                            case "SINKED" -> {
                                System.out.println("You have sinked opponent's ship!");
                                sendMessage("SINKED");
                                sendMessageToClient("OPPONENT_SHOT",currentRoom.getPlayerWhoDoesntPlay());
                                sendMessageToClient(position,currentRoom.getPlayerWhoDoesntPlay());
                            }
                            case "MISS" -> {
                                System.out.println("You have missed!");
                                sendMessage("MISS");
                                sendMessageToClient("OPPONENT_MISS",currentRoom.getPlayerWhoDoesntPlay());
                                sendMessageToClient(position,currentRoom.getPlayerWhoDoesntPlay());
                                currentRoom.setWhoToPlay(currentRoom.getPlayerWhoDoesntPlay());
                            }
                        }
                        if(currentRoom.getArrayBasedOnPlayerWhoDoesntPlay().isEmpty()){
                            currentRoom.setGameOver(true);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        sendMessage("Position unavailable");
                    }
                }
            }

            if(this == currentRoom.getWhoToPlay()){
                sendMessage("WIN_PHASE");
            }
            else{
                sendMessage("LOSE_PHASE");
            }

        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            throw new RuntimeException(e);

            // TODO : handle
        }

    }

    public void gameBoardsSetter() throws IOException, ClassNotFoundException {
        SerializableArrayList receivedData = (SerializableArrayList) objectInputStream.readObject();
        ArrayList<ArrayList<String>> data = receivedData.getData();
        if(currentRoom.getHost() == this){
            currentRoom.setHostArrayList(data);
            currentRoom.getLatchPlacingPhase().countDown();
        }
        else if(currentRoom.getPlayer2() == this){
            currentRoom.setPlayer2ArrayList(data);
            currentRoom.getLatchPlacingPhase().countDown();
        }
    }

    public void sendMessage(String message) throws IOException {
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    public void sendMessageToClient(String message, ClientHandler clientHandler) throws IOException {
        clientHandler.bufferedWriter.write(message);
        clientHandler.bufferedWriter.newLine();
        clientHandler.bufferedWriter.flush();
    }

    public void latchWaiter(CountDownLatch latchToCheck, int valueToCheck) throws InterruptedException {
        while (latchToCheck.getCount() > valueToCheck) {
            Thread.sleep(1000);
        }
    }
    public String processShot(String position, ArrayList<ArrayList<String>> playerArrayPositions) {
        String pos = position.toUpperCase();
        String searchedPosition;
        for(ArrayList<String> tempArray : playerArrayPositions){
            searchedPosition = tempArray.stream()
                    .filter(s -> s.equals(pos))
                    .findFirst()
                    .orElse("NULL");
            if(!searchedPosition.equals("NULL")) {
                tempArray.remove(pos);
                if(!tempArray.isEmpty()) return "SHOT";
                else {
                    playerArrayPositions.remove(tempArray);
                    return "SINKED";
                }
            }
        }
        return "MISS";
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
    }

    public void removeRoom(){
        server.getRooms().remove(currentRoom);
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter, ObjectInputStream objectInputStream) {
        removeClientHandler();
        removeRoom();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if(objectInputStream != null){
                objectInputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
