package com.example.shipsgamegui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class ClientApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("client-welcome-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setMaxWidth(1000);
        stage.setMaxHeight(800);

        stage.setTitle("BATTLESHIPS GAME");
        stage.setScene(scene);
        stage.show();

        ClientSocketConnection.initialize();

    }

    public static void main(String[] args) {
        launch();
    }
}
