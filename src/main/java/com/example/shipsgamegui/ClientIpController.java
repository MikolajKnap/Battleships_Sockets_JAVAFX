package com.example.shipsgamegui;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import java.util.concurrent.CompletableFuture;


public class ClientIpController {
    public TextField textfield_ip;

    public void handleIPConfirm() {
        String enteredIp = textfield_ip.getText();
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
            return ClientSocketConnection.initialize(enteredIp);
        });

        future.thenAccept(result -> Platform.runLater(() -> {
            if(result){
                ClientGUISettings.initializeNewWindow("client-welcome-view.fxml", "WELCOME TO THE BATTLESHIPS", textfield_ip);
            }
            else{
                ClientGUISettings.showAlert("Server not responding");
            }
        }));
    }
}
