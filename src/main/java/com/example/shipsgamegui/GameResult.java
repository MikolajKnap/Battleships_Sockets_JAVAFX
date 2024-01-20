package com.example.shipsgamegui;

public class GameResult {
    private String host,player,winner, data;

    public GameResult(String host, String player, String winner, String data) {
        this.host = host;
        this.player = player;
        this.winner = winner;
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
}