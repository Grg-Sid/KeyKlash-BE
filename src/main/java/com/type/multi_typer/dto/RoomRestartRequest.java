package com.type.multi_typer.dto;

import jakarta.validation.constraints.NotBlank;

public class RoomRestartRequest {
    @NotBlank
    private String roomCode;
    @NotBlank
    private String newText;

    public RoomRestartRequest() {}

    public RoomRestartRequest(String roomCode, String newText) {
        this.roomCode = roomCode;
        this.newText = newText;
    }

    public String getRoomCode() {
        return roomCode;
    }
    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }
    public String getNewText() {
        return newText;
    }
    public void setNewText(String newText) {
        this.newText = newText;
    }
}
