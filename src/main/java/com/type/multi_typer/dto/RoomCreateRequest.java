package com.type.multi_typer.dto;

import jakarta.validation.constraints.NotBlank;

public class RoomCreateRequest {
    @NotBlank
    private int maxPlayers;
    private String creatorName;

    public RoomCreateRequest() {}

    public RoomCreateRequest(int maxPlayers, String creatorName) {
        this.maxPlayers = maxPlayers;
        this.creatorName = creatorName;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getCreatorName() {
        return creatorName;
    }
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
}
