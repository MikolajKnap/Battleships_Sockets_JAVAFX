package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Server {
    private ServerSocket serverSocket;
    private ArrayList<Room> rooms;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.rooms = new ArrayList<>();
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("****New client has connected****");
                ClientHandler clientHandler = new ClientHandler(socket, this);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            stopServer();
        }
    }

    public void stopServer() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized ArrayList<Room> getRooms() {
        return rooms;
    }


    public synchronized Room createRoom(String roomName, ClientHandler host) {
        Room room = new Room(roomName, host);
        rooms.add(room);
        return room;
    }

    public synchronized ArrayList<String> getRoomsString() {
        ArrayList<String> arr = new ArrayList<>();
        for(Room r : rooms){
            arr.add(r.getRoomName());
        }
        return arr;
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
