package com.example.shipsgamegui;

import com.example.shipsgamegui.ClientSocketConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ClientMainMenuController {
    @FXML
    public Label label_menu;
    public Button buttonCreateRoom;

    public Button buttonJoinRoom;
    public Button buttonViewRooms;
    public Label label_roomCreated;
    public Label label_waitingRoom;

    @FXML
    private void handleCreateRoom(ActionEvent event) {
        ClientSocketConnection.sendMessage("1");

        label_menu.setVisible(false);
        buttonCreateRoom.setVisible(false);
        buttonJoinRoom.setVisible(false);
        buttonViewRooms.setVisible(false);

        label_waitingRoom.setText("Waiting for other player");


        // Uruchom wątek, który czeka na odpowiedź od serwera
        Thread responseThread = new Thread(() -> {
            String response = ClientSocketConnection.readMessage();
            Platform.runLater(() -> handleServerResponse(response));
        });
        responseThread.start();
    }

    private void handleServerResponse(String response) {
        try {
            if ("PLACE_PHASE".equals(response)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("client-placeships.fxml"));
                Parent root = loader.load();

                // Tworzenie nowego okna
                Stage mainMenuStage = new Stage();
                mainMenuStage.setTitle("PLACE SHIPS");
                mainMenuStage.setScene(new Scene(root, 800, 600));
                mainMenuStage.setMinWidth(800);
                mainMenuStage.setMinHeight(600);
                mainMenuStage.setMaxWidth(1000);
                mainMenuStage.setMaxHeight(800);

                // Pokazanie nowego okna
                mainMenuStage.show();

                // Zamknięcie obecnej sceny (okna)
                Stage currentStage = (Stage) label_menu.getScene().getWindow();
                currentStage.close();
            } else {
                System.out.println("Odpowiedź od serwera: " + response);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleJoinRoom(ActionEvent event) {
        ClientSocketConnection.sendMessage("3");
        try {
            // Ładowanie pliku FXML dla kolejnego widoku
            FXMLLoader loader = new FXMLLoader(getClass().getResource("client-join-room.fxml"));
            Parent root = loader.load();

            // Tworzenie nowego okna
            Stage mainMenuStage = new Stage();
            mainMenuStage.setTitle("JOIN ROOM");
            mainMenuStage.setScene(new Scene(root, 800, 600));
            mainMenuStage.setMinWidth(800);
            mainMenuStage.setMinHeight(600);
            mainMenuStage.setMaxWidth(1000);
            mainMenuStage.setMaxHeight(800);

            // Pokazanie nowego okna
            mainMenuStage.show();

            // Zamknięcie obecnej sceny (okna)
            Stage currentStage = (Stage) label_menu.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleViewRooms(ActionEvent event) throws IOException, ClassNotFoundException {
        ClientSocketConnection.sendMessage("3");

        try {
            // Ładowanie pliku FXML dla kolejnego widoku
            FXMLLoader loader = new FXMLLoader(getClass().getResource("client-rooms-list.fxml"));
            Parent root = loader.load();

            // Tworzenie nowego okna
            Stage mainMenuStage = new Stage();
            mainMenuStage.setTitle("ROOMS LIST");
            mainMenuStage.setScene(new Scene(root, 800, 600));
            mainMenuStage.setMinWidth(800);
            mainMenuStage.setMinHeight(600);
            mainMenuStage.setMaxWidth(1000);
            mainMenuStage.setMaxHeight(800);

            // Pokazanie nowego okna
            mainMenuStage.show();

            // Zamknięcie obecnej sceny (okna)
            Stage currentStage = (Stage) label_menu.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
