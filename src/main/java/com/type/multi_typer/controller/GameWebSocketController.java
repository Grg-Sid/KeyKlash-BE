package com.type.multi_typer.controller;

import com.type.multi_typer.dto.GameMessage;
import com.type.multi_typer.dto.MessageType;
import com.type.multi_typer.dto.TypingUpdate;
import com.type.multi_typer.model.Room;
import com.type.multi_typer.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GameWebSocketController {

    private static final Logger log = LoggerFactory.getLogger(GameWebSocketController.class);
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private GameService gameService;

    @MessageMapping("/game/progress")
    public void handleTypingUpdate(@Payload TypingUpdate typingUpdate) {
        // Update the state in service.
        gameService.updatePlayerProgress(typingUpdate.getRoomId(), typingUpdate.getPlayerId(), typingUpdate);

        // Get the new complete state
        Room updatedRoom = gameService.getRoom(typingUpdate.getRoomId());

        // Broadcast it
        if (updatedRoom != null) {
            GameMessage gameMessage = new GameMessage(MessageType.ROOM_UPDATE, updatedRoom, updatedRoom.getId(), typingUpdate.getPlayerId());
            messagingTemplate.convertAndSend("/topic/room/" + updatedRoom.getId(), gameMessage);
        }
    }

    @MessageMapping("/game/start")
    public void handleGameStart(@Payload GameMessage gameMessage) {
        // Call service method
        gameService.startGame(gameMessage.getRoomId());

        // Get new state
        Room updatedRoom = gameService.getRoom(gameMessage.getRoomId());

        // Broadcast it
        if (updatedRoom != null) {
            GameMessage response = new GameMessage(MessageType.GAME_STARTED, updatedRoom, updatedRoom.getId(), gameMessage.getPlayerId());
            messagingTemplate.convertAndSend("/topic/room/" + updatedRoom.getId(), response);
        }
    }

    @MessageMapping("/game/ready")
    public void handleGameReady(@Payload GameMessage readyMessage) {
        gameService.playerReady(readyMessage.getRoomId(), readyMessage.getPlayerId());
        Room updatedRoom = gameService.getRoom(readyMessage.getRoomId());

    }
}
