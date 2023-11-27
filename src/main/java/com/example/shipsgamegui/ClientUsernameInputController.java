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

        if (enteredUsername.length() > 15 || enteredUsername.isEmpty()) {
            showAlert("Enter valid nickname!");
        }
        else {
            System.out.printf(enteredUsername);
            ClientSocketConnection.sendMessage(enteredUsername);
            openMainMenu();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openMainMenu() {
        try {
            // Ładowanie pliku FXML dla kolejnego widoku
            FXMLLoader loader = new FXMLLoader(getClass().getResource("client-main-menu.fxml"));
            Parent root = loader.load();

            // Tworzenie nowego okna
            Stage mainMenuStage = new Stage();
            mainMenuStage.setTitle("MENU");
            mainMenuStage.setScene(new Scene(root, 800, 600));
            mainMenuStage.setMinWidth(800);
            mainMenuStage.setMinHeight(600);
            mainMenuStage.setMaxWidth(1000);
            mainMenuStage.setMaxHeight(800);

            // Pokazanie nowego okna
            mainMenuStage.show();

            // Zamknięcie obecnej sceny (okna)
            Stage currentStage = (Stage) usernameTextField.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}