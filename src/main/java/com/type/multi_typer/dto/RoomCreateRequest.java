package com.type.multi_typer.dto;

import jakarta.validation.constraints.NotBlank;

public class RoomCreateRequest {
    @NotBlank
    private String creatorName;
    private String text;

    public RoomCreateRequest() {}

    public RoomCreateRequest(String creatorName, String text) {
        this.creatorName = creatorName;
        this.text = text;
    }

    public String getCreatorName() {
        return creatorName;
    }
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
}
