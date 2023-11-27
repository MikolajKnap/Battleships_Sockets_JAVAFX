package com.example.shipsgamegui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ClientRoomsListController implements Initializable {
    public Label label_roomsList;
    public Button button_return;
    public TextArea textarea_roomsList;
    public ListView listview_roomsList;


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
        ObservableList<String> items = FXCollections.observableArrayList(roomList);
        listview_roomsList.setItems(items);
    }
}
