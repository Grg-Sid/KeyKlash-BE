package com.type.multi_typer.controller;

import com.type.multi_typer.dto.GameMessage;
import com.type.multi_typer.dto.MessageType;
import com.type.multi_typer.dto.TypingUpdate;
import com.type.multi_typer.model.Room;
import com.type.multi_typer.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GameWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private GameService gameService;

    @MessageMapping("/typing-update")
    public void handleTypingUpdate(@Payload TypingUpdate typingUpdate) {
        try {
            Room room = gameService.getRoom(typingUpdate.getRoomId());
            if (room != null) {
                gameService.updatePlayerProgress(room.getId(), typingUpdate.getPlayerId(), typingUpdate);

                GameMessage gameMessage = new GameMessage(MessageType.TYPING_UPDATE, room, room.getId(), typingUpdate.getPlayerId());
                messagingTemplate.convertAndSend("/topic/room/" + room.getId() , gameMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MessageMapping("/join-room")
    public void handleJoinRoom(@Payload GameMessage gameMessage) {
        try {
            GameMessage response = new GameMessage(MessageType.PLAYER_JOINED, gameMessage.getPayload(), gameMessage.getRoomId(), gameMessage.getPlayerId());
            messagingTemplate.convertAndSend("/topic/room/" + gameMessage.getRoomId(), response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MessageMapping("/leave-room")
    public void handleLeaveRoom(@Payload GameMessage gameMessage) {
        try {
            gameService.leaveRoom(gameMessage.getRoomId(), gameMessage.getPlayerId());

            GameMessage response = new GameMessage(MessageType.PLAYER_LEFT, gameMessage.getPayload(), gameMessage.getRoomId(), gameMessage.getPlayerId());
            messagingTemplate.convertAndSend("/topic/room/" + gameMessage.getRoomId(), response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MessageMapping("/start-game")
    public void handleStartGame(@Payload GameMessage gameMessage) {
        try {
            gameService.startGame(gameMessage.getRoomId());
            Room room = gameService.getRoom(gameMessage.getRoomId());

            GameMessage response = new GameMessage(MessageType.GAME_STARTED, room, gameMessage.getPlayerId(), gameMessage.getRoomId());
            messagingTemplate.convertAndSend("/topic/room/" + gameMessage.getRoomId(), response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
