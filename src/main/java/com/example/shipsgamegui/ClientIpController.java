package com.example.shipsgamegui;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;


public class ClientIpController {
    public TextField textfield_ip;

    public void handleIPConfirm() {
        String enteredIp = textfield_ip.getText();
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
            try {
                ClientSocketConnection.initialize(enteredIp);
                return true;
            }
            catch (IOException e) {
                return false;
            }
        });

        future.thenApply(result -> {
            Platform.runLater(() -> {
                if(result){
                    ClientGUISettings.initializeNewWindow("client-welcome-view.fxml", "WELCOME TO THE BATTLESHIPS", textfield_ip);
                }
                else{
                    ClientGUISettings.showAlert("Server not responding");
                }
            });
            return null;
        });
    }
}
