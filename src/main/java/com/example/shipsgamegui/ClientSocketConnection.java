package com.example.shipsgamegui;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientSocketConnection {
    public static Socket socket;
    public static ObjectOutputStream objectOutputStream;
    public static ObjectInputStream objectInputStream;
    public static BufferedReader bufferedReader;
    public static BufferedWriter bufferedWriter;
    private static ArrayList<ArrayList<String>> ownBoard;

    public static boolean initialize(String ip) {
        try{
            socket = new Socket(ip,1234);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            ownBoard = new ArrayList<>();

            return true;
        }
        catch (IOException e){
            close();
            return false;
        }

    }
    public static void sendMessage(String message){
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            close();
        }

    }


    public static String readMessage() {
        try {
            return bufferedReader.readLine();
        }
        catch (IOException e) {
            close();
            return null;
        }
    }

    public static void close() {
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
            if(objectOutputStream != null){
                objectOutputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getArrayListFromObject() {
        SerializableArrayList receivedData = null;
        try {
            receivedData = (SerializableArrayList) objectInputStream.readObject();
        }
        catch (IOException | ClassNotFoundException e) {
            close();
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
            close();
        }
    }

    public static ArrayList<ArrayList<String>> getOwnBoard() {
        return ownBoard;
    }

    public static void setOwnBoard(ArrayList<ArrayList<String>> ownBoard) {
        ClientSocketConnection.ownBoard = ownBoard;
    }
}
