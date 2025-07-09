package com.type.multi_typer.model.ws;

public class TypingUpdateMessage {
    private String playerId;
    private int progress;

    public TypingUpdateMessage(String playerId, int progress) {
        this.playerId = playerId;
        this.progress = progress;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
