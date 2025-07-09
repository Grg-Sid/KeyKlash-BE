package com.type.multi_typer.dto;

import jakarta.validation.constraints.NotBlank;

public class RoomCreateRequest {
    @NotBlank
    private int maxPlayers;

    public RoomCreateRequest() {}

    public RoomCreateRequest(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
}
