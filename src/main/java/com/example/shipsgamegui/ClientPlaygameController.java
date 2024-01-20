package com.example.shipsgamegui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import java.util.ArrayList;


public class ClientPlaygameController {
    private final int gridSize = 10;

    public Canvas canvas_shooting;
    public Canvas canvas_your;
    public Label label_game;
    private double cellSize;
    String flag;
    private String lastShot;

    private int columnIndex;
    private int rowIndex;

    private long lastClickTime = 0;
    private final int CLICK_INTERVAL = 500;


    @FXML
    public void initialize() {
        flag = ClientSocketConnection.readMessage();
        // Inicjalizacja rozmiaru komórki po uzyskaniu szerokości canvas
        cellSize = canvas_shooting.getWidth() / gridSize;
        Thread responseThread = new Thread(this::handleServerResponse);
        responseThread.setDaemon(true); // Ustawienie wątku jako daemon zapewnia, że zostanie zakończony, gdy główny wątek aplikacji zostanie zakończony.
        responseThread.start();

        // Rysowanie planszy po uzyskaniu szerokości canvas
        drawGrid(canvas_shooting);
        drawGrid(canvas_your);

        for(ArrayList<String> p : ClientSocketConnection.getOwnBoard()){
            for(String s : p){
                int sx = Character.getNumericValue(s.charAt(0));
                int sy = Character.getNumericValue(s.charAt(1));
                drawShip(sx,sy,canvas_your, Color.rgb(40,160,200));
            }
        }

        // Ustawienie obsługi zdarzeń kliknięcia myszą po uzyskaniu canvas
        setMouseClickEvent();


    }

    private void handleServerResponse() {
        while(ClientSocketConnection.socket.isConnected()){
            String message = ClientSocketConnection.readMessage();
            if(message.equals("SHOT")){
                drawShip(rowIndex, columnIndex, canvas_shooting, Color.rgb(40,160,200));
            }
            else if(message.equals("SINKED")){
                drawShip(rowIndex, columnIndex, canvas_shooting, Color.rgb(210,40,40));
            }
            else if(message.equals("MISS")){
                drawShip(rowIndex, columnIndex, canvas_shooting, Color.rgb(98,98,98));
                flag = "wait";
            }
            else if(message.equals("OPPONENT_SHOT")){
                String s = ClientSocketConnection.readMessage();
                int sx = Character.getNumericValue(s.charAt(0));
                int sy = Character.getNumericValue(s.charAt(1));
                drawShip(sx,sy,canvas_your, Color.rgb(130,0,0));
                flag = "wait";
            }
            else if(message.equals("OPPONENT_MISS")){
                String s = ClientSocketConnection.readMessage();
                int sx = Character.getNumericValue(s.charAt(0));
                int sy = Character.getNumericValue(s.charAt(1));
                drawShip(sx,sy,canvas_your, Color.rgb(130,130,130));
                flag = "play";
            }
            else if(message.equals("WIN_PHASE")){
                flag = "win";
                Platform.runLater(() -> {
                    label_game.setText("YOU HAVE WON!");
                });
                fillBoard(canvas_your,Color.GOLD);
                fillBoard(canvas_shooting,Color.GOLD);
                break;
            }
            else if(message.equals("LOSE_PHASE")){
                flag = "lose";
                Platform.runLater(() -> {
                    label_game.setText("YOU HAVE LOST!");
                });
                fillBoard(canvas_your,Color.GRAY);
                fillBoard(canvas_shooting,Color.GRAY);
                break;
            }

        }
    }

    private void setMouseClickEvent() {
        canvas_shooting.setOnMouseClicked(event -> handleMouseClick(event.getX(), event.getY()));
    }

    private void handleMouseClick(double x, double y) {
        long currentTime = System.currentTimeMillis();

        // Sprawdź, czy wystąpiło wystarczająco dużo czasu od ostatniego kliknięcia
        if (currentTime - lastClickTime >= CLICK_INTERVAL) {
            columnIndex = (int) (x / cellSize);
            rowIndex = (int) (y / cellSize);
            if(flag.equals("play")){
                lastShot = String.format("%d%d",rowIndex,columnIndex);
                ClientSocketConnection.sendMessage(String.format("%d%d",rowIndex,columnIndex));
                lastClickTime = currentTime;
            }
            else{
                if(flag.equals("win")){
                    label_game.setText("YOU HAVE WON");
                }
                else if(flag.equals("lose")){
                    label_game.setText("YOU HAVE LOST");
                }
                else{
                    ClientGUISettings.showAlert("Wait for your turn!");
                }
            }
        }
    }

    private void drawGrid(Canvas canva) {
        GraphicsContext gc = canva.getGraphicsContext2D();

        for (int i = 0; i <= gridSize; i++) {
            double x = i * cellSize;
            double y = i * cellSize;

            // Rysowanie linii pionowych
            gc.strokeLine(x, 0, x, canva.getHeight());

            // Rysowanie linii poziomych
            gc.strokeLine(0, y, canva.getWidth(), y);
        }
    }

    private void drawShip(int rowIndex, int columnIndex, Canvas canva, Color color) {
        GraphicsContext gc = canva.getGraphicsContext2D();
        double x = rowIndex * cellSize;
        double y = columnIndex * cellSize;

        // Ustawienie koloru na przykład czerwony
        gc.setFill(color);
        gc.fillRect(y, x, cellSize, cellSize);
    }

    private void fillBoard(Canvas canva, Color color) {
        GraphicsContext gc = canva.getGraphicsContext2D();

        // Ustawienie koloru na przykład czerwony
        gc.setFill(color);
        gc.fillRect(0, 0, cellSize*10, cellSize*10);
    }

}
