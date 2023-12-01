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
        ClientGUISettings.startWindow("client-ip.fxml", "SERVER");
    }

    public static void main(String[] args) {
        launch();
    }
}
