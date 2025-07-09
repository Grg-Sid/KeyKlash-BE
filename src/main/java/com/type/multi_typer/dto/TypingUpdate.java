package com.type.multi_typer.dto;

public class TypingUpdate {
    private String roomId;
    private String playerId;
    private int currentPosition;
    private String typedText;

    public TypingUpdate() {}

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
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

    public String getTypedText() {
        return typedText;
    }

    public void setTypedText(String typedText) {
        this.typedText = typedText;
    }

    public TypingUpdate(String playerId, int currentPosition, String typedText, String roomId) {
        this.playerId = playerId;
        this.currentPosition = currentPosition;
        this.typedText = typedText;
        this.roomId = roomId;
    }
}
