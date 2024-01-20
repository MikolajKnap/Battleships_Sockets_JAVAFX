package server;

import com.example.shipsgamegui.SerializableArrayList;
import database.GameDatabase;

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

    private boolean errorFlag;


    public ClientHandler(Socket socket, Server server) {
        try {
            this.socket = socket;
            this.server = server;

            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            this.errorFlag = false;

            this.username = null;

            clientHandlers.add(this);

        } catch (IOException e) {
            closeEverything();
        }
    }

    private void usernamePhase() throws IOException, ClassNotFoundException {
        while(username == null){
            username = bufferedReader.readLine();
            synchronized (usernamesList){
                if(!usernamesList.contains(username)){
                    sendMessage("ACK");
                    usernamesList.add(username);
                }
                else{
                    username = null;
                    sendMessage("NACK");
                }
            }
        }
    }

    private void menuPhase() throws IOException, InterruptedException, NullPointerException {
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
                    }
                    else {
                        sendMessage("ERROR");
                    }
                }
                case "3" -> {
                    ArrayList<ArrayList<String>> arrayToSend = new ArrayList<>();
                    arrayToSend.add(server.getRoomsString());
                    SerializableArrayList serializableArrayToSend = new SerializableArrayList(arrayToSend);
                    objectOutputStream.writeObject(serializableArrayToSend);
                }
                default -> sendMessage("ERROR");
            }
        }
    }

    private void hostPlacePhaseSetter() throws IOException {
        if(currentRoom.getHost() == this){
            sendMessage("PLACE_PHASE");
        }
    }

    private void gamePhaseSetter() throws IOException {
        sendMessage("GAME_PHASE");
        if(currentRoom.getHost() == this){
            sendMessage("play");
        }
        else if(currentRoom.getPlayer2() == this){
            sendMessage("wait");
        }
    }

    private void gameLogic() throws IOException, InterruptedException {
        while (!currentRoom.isGameOver() && socket.isConnected()) {
            Thread.sleep(1000);
            if(!clientHandlers.contains(currentRoom.getHost()) || !clientHandlers.contains(currentRoom.getPlayer2())){
                errorFlag = true;
                break;
            }
            if (currentRoom.getWhoToPlay() == this) {
                String position = bufferedReader.readLine(); //a1
                String processedShot = processShot(position, currentRoom.getArrayBasedOnPlayerWhoDoesntPlay());
                switch (processedShot) {
                    case "SHOT" -> {
                        sendMessage("SHOT");
                        sendMessageToClient("OPPONENT_SHOT",currentRoom.getPlayerWhoDoesntPlay());
                        sendMessageToClient(position,currentRoom.getPlayerWhoDoesntPlay());
                    }
                    case "SINKED" -> {
                        sendMessage("SINKED");
                        sendMessageToClient("OPPONENT_SHOT",currentRoom.getPlayerWhoDoesntPlay());
                        sendMessageToClient(position,currentRoom.getPlayerWhoDoesntPlay());
                    }
                    case "MISS" -> {
                        sendMessage("MISS");
                        sendMessageToClient("OPPONENT_MISS",currentRoom.getPlayerWhoDoesntPlay());
                        sendMessageToClient(position,currentRoom.getPlayerWhoDoesntPlay());
                        currentRoom.setWhoToPlay(currentRoom.getPlayerWhoDoesntPlay());
                    }
                }
                if(currentRoom.getArrayBasedOnPlayerWhoDoesntPlay().isEmpty()){
                    currentRoom.setGameOver(true);
                }
            }
        }
    }


    @Override
    public void run() {
        try {


            usernamePhase();

            menuPhase();

            latchWaiter(currentRoom.getLatchRoomPhase(), 0);
            hostPlacePhaseSetter();

            gameBoardsSetter();

            latchWaiter(currentRoom.getLatchPlacingPhase(),0);

            gamePhaseSetter();

            gameLogic();

            if(this == currentRoom.getWhoToPlay() && !errorFlag){
                sendMessage("WIN_PHASE");
                GameDatabase.saveGameResult(currentRoom.getHost().username, currentRoom.getPlayer2().username,currentRoom.getWhoToPlay().username);
                closeEverything();
            }
            else if(this != currentRoom.getWhoToPlay() && !errorFlag){
                sendMessage("LOSE_PHASE");
                closeEverything();
            }
            if(errorFlag){
                closeEverything();
            }

        } catch (IOException | InterruptedException | ClassNotFoundException | NullPointerException e) {
            closeEverything();
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

    public void latchWaiter(CountDownLatch latchToCheck, int valueToCheck) throws InterruptedException, IOException {
        while (latchToCheck.getCount() > valueToCheck && socket.isConnected()) {
            Thread.sleep(1000);
            //TODO
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

    public void removeUsernameFromList(){
        usernamesList.remove(this.username);
    }

    public void closeEverything() {
        removeClientHandler();
        removeRoom();
        if(this.username != null){
            System.out.printf("Player: %s has left%n",username);
            removeUsernameFromList();
        }

        try {
            if (this.bufferedReader != null) {
                this.bufferedReader.close();
            }
            if (this.bufferedWriter != null) {
                this.bufferedWriter.close();
            }
            if(this.objectInputStream != null){
                this.objectInputStream.close();
            }
            if(this.objectOutputStream != null){
                this.objectOutputStream.close();
            }
            if (this.socket != null) {
                this.socket.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
