package com.type.multi_typer.dto;

public class RoomCreateResponse {
    private String roomId;
    private String joinUrl;

    public RoomCreateResponse(String roomId, String joinUrl) {
        this.roomId = roomId;
        this.joinUrl = joinUrl;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getJoinUrl() {
        return joinUrl;
    }

    public void setJoinUrl(String joinUrl) {
        this.joinUrl = joinUrl;
    }
}
