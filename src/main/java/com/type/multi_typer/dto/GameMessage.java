package com.type.multi_typer.dto;

public class GameMessage {
    private MessageType type;
    private Object payload;
    private String roomId;
    private String playerId;

    public GameMessage() {}

    public GameMessage(MessageType type, Object payload, String roomId, String playerId) {
        this.type = type;
        this.payload = payload;
        this.roomId = roomId;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

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
}
