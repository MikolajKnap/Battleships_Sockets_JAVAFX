package com.example.shipsgamegui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import java.util.concurrent.CompletableFuture;

public class ClientUsernameInputController {

    @FXML
    private TextField usernameTextField;

    @FXML
    private void handleUsernameConfirm(ActionEvent event) {
        String enteredUsername = usernameTextField.getText();
        if(enteredUsername.length() < 15 && !enteredUsername.isEmpty()){
            ClientSocketConnection.sendMessage(enteredUsername);

            CompletableFuture<String> future = CompletableFuture.supplyAsync(ClientSocketConnection::readMessage);
            future.thenAccept(result -> {
                Platform.runLater(() -> {
                    if(result.equals("ACK")){
                        ClientGUISettings.initializeNewWindow("client-main-menu.fxml","MENU", usernameTextField);                }
                    else{
                        ClientGUISettings.showAlert("Username taken");
                    }
                });
            });
        }
        else{
            ClientGUISettings.showAlert("Enter valid nickname!");
        }
    }
}