package com.type.multi_typer.dto;

public class RoomJoinRequest {
    private String nickname;
    private String roomCode;

    public RoomJoinRequest() {}

    public RoomJoinRequest(String nickname, String roomCode) {
        this.nickname = nickname;
        this.roomCode = roomCode;
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getRoomCode() {
        return roomCode;
    }
    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }
}
