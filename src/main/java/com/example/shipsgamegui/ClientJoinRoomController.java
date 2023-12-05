package com.example.shipsgamegui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class ClientJoinRoomController implements Initializable {
    @FXML
    public Label label_roomsList;
    public ComboBox comboBox;
    public Button button_confirm;

    public void handleReturnToMenu() {
        ClientGUISettings.initializeNewWindow("client-main-menu.fxml","MENU", label_roomsList);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        CompletableFuture<ArrayList<String>> future = CompletableFuture.supplyAsync(ClientSocketConnection::getArrayListFromObject);
        future.thenAccept(result -> Platform.runLater(() -> {
            ObservableList<String> items = FXCollections.observableArrayList(result);
            comboBox.setItems(items);
        }));

    }

    public void handleConfirm() {
        if(comboBox.getValue() != null){
            ClientSocketConnection.sendMessage("2");
            ClientSocketConnection.sendMessage((String) comboBox.getValue());

            CompletableFuture<String> future = CompletableFuture.supplyAsync(ClientSocketConnection::readMessage);
            future.thenAccept(result -> Platform.runLater(() -> {
                if(result.equals("ACK")){
                    ClientGUISettings.initializeNewWindow("client-placeships.fxml","PLACE SHIPS", label_roomsList);                }
                else{
                    ClientGUISettings.showAlert("This room is full");
                }
            }));
        }
        else{
            ClientGUISettings.showAlert("You need to choose room!");
        }
    }
}
