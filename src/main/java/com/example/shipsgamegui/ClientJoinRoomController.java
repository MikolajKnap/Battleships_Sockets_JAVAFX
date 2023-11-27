package com.example.shipsgamegui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ClientJoinRoomController implements Initializable {
    @FXML
    public Label label_roomsList;
    public ComboBox comboBox;
    public Button button_confirm;

    public void handleReturnToMenu(ActionEvent actionEvent) {
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
            Stage currentStage = (Stage) label_roomsList.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ArrayList<String> roomList = ClientSocketConnection.getArrayListFromObject();
        System.out.println(roomList.toString());
        ObservableList<String> items = FXCollections.observableArrayList(roomList);
        comboBox.setItems(items);
    }

    public void handleConfirm(ActionEvent actionEvent) {
        if(comboBox.getValue() != null){
            ClientSocketConnection.sendMessage("2");
            ClientSocketConnection.sendMessage((String) comboBox.getValue());
            if(ClientSocketConnection.readMessage().equals("ACK")){
                try {
                    // Ładowanie pliku FXML dla kolejnego widoku
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
                    Stage currentStage = (Stage) label_roomsList.getScene().getWindow();
                    currentStage.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                showAlert("This room is full");
            }
        }
        else{
            showAlert("You need to choose room!");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
