package com.example.shipsgamegui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.util.concurrent.CompletableFuture;

public class ClientMainMenuController {
    @FXML
    public Label label_menu;
    public Button buttonCreateRoom;

    public Button buttonJoinRoom;
    public Button buttonViewRooms;
    public Label label_roomCreated;
    public Label label_waitingRoom;

    public Button buttonViewGames;

    @FXML
    private void handleCreateRoom() {
        ClientSocketConnection.sendMessage("1");

        label_menu.setVisible(false);
        buttonCreateRoom.setVisible(false);
        buttonJoinRoom.setVisible(false);
        buttonViewRooms.setVisible(false);
        buttonViewGames.setVisible(false);


        label_waitingRoom.setText("Waiting for other player, time limit 1 minute");

        CompletableFuture<String> future = CompletableFuture.supplyAsync(ClientSocketConnection::readMessage);
        future.thenAccept(result -> Platform.runLater(() -> {
            if(result.equals("PLACE_PHASE")){
                ClientSocketConnection.sendMessage("ACK");
                ClientGUISettings.initializeNewWindow("client-placeships.fxml","PLACE SHIPS", label_menu);
            }
            else{
                ClientGUISettings.showAlert("Error");
            }
        }));
    }


    @FXML
    private void handleJoinRoom() {
        ClientSocketConnection.sendMessage("3");
        ClientGUISettings.initializeNewWindow("client-join-room.fxml","JOIN ROOM", label_menu);
    }
    @FXML
    private void handleViewRooms() {
        ClientSocketConnection.sendMessage("3");
        ClientGUISettings.initializeNewWindow("client-rooms-list.fxml","ROOMS LIST", label_menu);
    }

    @FXML
    private void handleViewGames() {
        ClientGUISettings.initializeNewWindow("client-view-games.fxml","LAST GAMES", label_menu);
    }
}
