package com.example.shipsgamegui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientUsernameInputController {

    @FXML
    private TextField usernameTextField;

    @FXML
    private void handleUsernameConfirm(ActionEvent event) {
        String enteredUsername = usernameTextField.getText();
        if(enteredUsername.length() < 15 && !enteredUsername.isEmpty()){
            ClientSocketConnection.sendMessage(enteredUsername);
            if(ClientSocketConnection.readMessage().equals("ACK")){
                ClientGUISettings.initializeNewWindow("client-main-menu.fxml","MENU", usernameTextField);
            }
            else{
                ClientGUISettings.showAlert("Username taken");
            }
        }
        else{
            ClientGUISettings.showAlert("Enter valid nickname!");
        }
    }
}