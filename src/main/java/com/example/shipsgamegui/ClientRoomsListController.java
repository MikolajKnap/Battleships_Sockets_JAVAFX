package com.example.shipsgamegui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class ClientRoomsListController implements Initializable {
    public Label label_roomsList;
    public Button button_return;
    public TextArea textarea_roomsList;
    public ListView listview_roomsList;


    public void handleReturnToMenu(ActionEvent actionEvent) {
        ClientGUISettings.initializeNewWindow("client-main-menu.fxml","MENU", label_roomsList);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        CompletableFuture<ArrayList<String>> future = CompletableFuture.supplyAsync(ClientSocketConnection::getArrayListFromObject);
        future.thenAccept(result -> Platform.runLater(() -> {
            ObservableList<String> items = FXCollections.observableArrayList(result);
            listview_roomsList.setItems(items);
        }));
    }
}
