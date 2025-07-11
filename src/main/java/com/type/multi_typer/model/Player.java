package com.type.multi_typer.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Player {
    private String id;
    private String nickname;
    private String roomId;
    private int currentPosition;
    private int wpm;
    private double accuracy;
    private boolean ready;
    private boolean finished;
    private LocalDateTime joinedAt;
    private LocalDateTime finishedAt;
    private String sessionId;

    private String generatePlayerId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public Player() {}

    public Player(String nickname, String roomId) {
        this.id = this.generatePlayerId();
        this.nickname = nickname;
        this.roomId = roomId;
        this.currentPosition = 0;
        this.wpm = 0;
        this.accuracy = 0.0;
        this.finished = false;
        this.joinedAt = LocalDateTime.now();
        this.ready = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
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

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean getReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
