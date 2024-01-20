package com.example.shipsgamegui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import database.GameDatabase;

public class ClientViewGames implements Initializable {

    @FXML
    private TableView<GameResult> tableView;
    @FXML
    public Label label_viewgames;

    @FXML
    public TableColumn<GameResult, String> hostColumn;

    @FXML
    public TableColumn<GameResult, String> player2Column;

    @FXML
    public TableColumn<GameResult, String> winnerColumn;

    @FXML
    public TableColumn<GameResult, String> dataColumn;

    public void handleReturnToMenu(ActionEvent actionEvent) {
        ClientGUISettings.initializeNewWindow("client-main-menu.fxml","MENU", label_viewgames);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        CompletableFuture<List<GameResult>> future = CompletableFuture.supplyAsync(() -> {
            hostColumn.setCellValueFactory(new PropertyValueFactory<>("host"));
            player2Column.setCellValueFactory(new PropertyValueFactory<>("player"));
            winnerColumn.setCellValueFactory(new PropertyValueFactory<>("winner"));
            dataColumn.setCellValueFactory(new PropertyValueFactory<>("data"));

            // Uzupełnij tabelę danymi z bazy danych
            return GameDatabase.getGameResults();
        });
        future.thenAccept(result -> Platform.runLater(() -> {
            tableView.getItems().addAll(result);
        }));


    }
}
