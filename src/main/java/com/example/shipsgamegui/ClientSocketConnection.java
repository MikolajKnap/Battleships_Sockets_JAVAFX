package com.example.shipsgamegui;

import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class ClientSocketConnection {
    public static Socket socket;
    public static ObjectOutputStream objectOutputStream;
    public static ObjectInputStream objectInputStream;
    public static BufferedReader bufferedReader;
    public static BufferedWriter bufferedWriter;
    private static CompletableFuture<String> responseFuture;

//    public static boolean listening = false;
//    public static String lastReceivedMessageFromServer;

    private static ArrayList<ArrayList<String>> ownBoard;

    public static void initialize(String ip) throws IOException {
        socket = new Socket(ip,1234);
        // = true;
        //startListening();
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        responseFuture = new CompletableFuture<>();
        ownBoard = new ArrayList<>();
    }
    public static void sendMessage(String message){
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
            // TODO : handle
        }

    }

//    public static void startListening(){
//        CompletableFuture.runAsync(() -> {
//            while(socket.isConnected() && listening){
//                lastReceivedMessageFromServer = readMessage();
//            }
//        });
//    }

    public static String readMessage() {
        try {
            return bufferedReader.readLine();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        // TODO : finish
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
            if (objectInputStream != null) {
                objectInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getArrayListFromObject() {
        SerializableArrayList receivedData = null;
        try {
            receivedData = (SerializableArrayList) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
            // TODO : handle
        }
        ArrayList<ArrayList<String>> data = receivedData.getData();
        return data.get(0);
    }

    public static void sendArrayListArrString(ArrayList<ArrayList<String>> arrayToSend) {
        SerializableArrayList serializableArrayToSend = new SerializableArrayList(arrayToSend);
        try {
            objectOutputStream.writeObject(serializableArrayToSend);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void receiveMessageAndCompleteFuture() {
        String message = readMessage();
        responseFuture.complete(message);
    }

    public static ArrayList<ArrayList<String>> getOwnBoard() {
        return ownBoard;
    }

    public static void setOwnBoard(ArrayList<ArrayList<String>> ownBoard) {
        ClientSocketConnection.ownBoard = ownBoard;
    }



}
