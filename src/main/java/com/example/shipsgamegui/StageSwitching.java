package com.example.shipsgamegui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Stage;

import java.io.IOException;

public class StageSwitching {
    public static void switchStage(String fxmlFilePath, String newStageTitle, Control prevStageControl) {
        FXMLLoader loader = new FXMLLoader(StageSwitching.class.getResource(fxmlFilePath));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Tworzenie nowego okna
        Stage mainMenuStage = new Stage();
        mainMenuStage.setTitle(newStageTitle);
        mainMenuStage.setScene(new Scene(root, 800, 600));
        mainMenuStage.setMinWidth(800);
        mainMenuStage.setMinHeight(600);
        mainMenuStage.setMaxWidth(1000);
        mainMenuStage.setMaxHeight(800);

        // Pokazanie nowego okna
        mainMenuStage.show();

        // Zamknięcie obecnej sceny (okna)
        Stage currentStage = (Stage) prevStageControl.getScene().getWindow();
        currentStage.close();
    }
}
