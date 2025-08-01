package com.type.multi_typer.dto;

import jakarta.validation.constraints.NotBlank;

public class RoomRestartRequest {
    @NotBlank
    private String roomId;
    @NotBlank
    private String newText;

    public RoomRestartRequest() {}

    public RoomRestartRequest(String roomId, String newText) {
        this.roomId = roomId;
        this.newText = newText;
    }

    public String getRoomId() {
        return roomId;
    }
    public void setRoomCode(String roomId) {
        this.roomId = roomId;
    }
    public String getNewText() {
        return newText;
    }
    public void setNewText(String newText) {
        this.newText = newText;
    }
}
