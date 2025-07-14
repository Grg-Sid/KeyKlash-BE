package com.type.multi_typer.dto;

public class PlayerProgressUpdate {
    private String playerId;
    private int currentPosition;
    private int wpm;
    private double accuracy;

    public PlayerProgressUpdate() {}

    public PlayerProgressUpdate(String playerId, int currentPosition, int wpm, double accuracy) {
        this.playerId = playerId;
        this.currentPosition = currentPosition;
        this.wpm = wpm;
        this.accuracy = accuracy;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getWpm() {
        return wpm;
    }

    public void setWpm(int wpm) {
        this.wpm = wpm;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }
}
