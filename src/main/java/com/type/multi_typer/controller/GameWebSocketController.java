package com.type.multi_typer.controller;

import com.type.multi_typer.dto.GameMessage;
import com.type.multi_typer.dto.MessageType;
import com.type.multi_typer.dto.RoomRestartRequest;
import com.type.multi_typer.dto.TypingUpdate;
import com.type.multi_typer.model.Room;
import com.type.multi_typer.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GameWebSocketController {

    private static final Logger log = LoggerFactory.getLogger(GameWebSocketController.class);

    private final SimpMessagingTemplate messagingTemplate;

    private final GameService gameService;

    public GameWebSocketController(SimpMessagingTemplate simpMessagingTemplate, GameService gameService) {
        this.messagingTemplate = simpMessagingTemplate;
        this.gameService = gameService;
    }

    @MessageMapping("/game/progress")
    public void handleTypingUpdate(@Payload TypingUpdate typingUpdate) {
        boolean isFinished = gameService.updatePlayerProgress(
                typingUpdate.getRoomId(),
                typingUpdate.getPlayerId(),
                typingUpdate
        );

        GameMessage progressMessage = new GameMessage(
                MessageType.PLAYER_PROGRESS,
                typingUpdate,
                typingUpdate.getRoomId(),
                typingUpdate.getPlayerId()
        );
        messagingTemplate.convertAndSend("/topic/room/" + typingUpdate.getRoomId(), progressMessage);

        if (isFinished) {
            GameMessage finishMessage = new GameMessage(
                    MessageType.PLAYER_FINISHED,
                    typingUpdate,
                    typingUpdate.getRoomId(),
                    typingUpdate.getPlayerId()
            );
            messagingTemplate.convertAndSend("/topic/room/" + typingUpdate.getRoomId(), finishMessage);

            if (gameService.hasAllPlayerFinished(typingUpdate.getRoomId())) {
                GameMessage gameOverMessage = new GameMessage(
                        MessageType.GAME_OVER,
                        null,
                        typingUpdate.getRoomId(),
                        typingUpdate.getPlayerId()
                );
                messagingTemplate.convertAndSend("/topic/room/" + typingUpdate.getRoomId(), gameOverMessage);
            }
        }
    }

    @MessageMapping("/game/restart")
    public void handleGameRestart(@Payload RoomRestartRequest roomRestartRequest) {
        String roomId = roomRestartRequest.getRoomId();
        if (gameService.hasAllPlayerFinished(roomId)) {
            gameService.resetRoom(roomId, roomRestartRequest.getNewText());
            Room updatedRoom = gameService.getRoom(roomId);
            GameMessage restartMessage = new GameMessage(
            MessageType.GAME_RESTART,
                    updatedRoom,
                    updatedRoom.getId(),
                    updatedRoom.getCreatedBy().getId()
            );
            messagingTemplate.convertAndSend("/topic/room/" + roomId, restartMessage);
        }
    }


    @MessageMapping("/game/start")
    public void handleGameStart(@Payload GameMessage gameMessage) {
        gameService.startGame(gameMessage.getRoomId());

        Room updatedRoom = gameService.getRoom(gameMessage.getRoomId());

        if (updatedRoom != null) {
            GameMessage response = new GameMessage(MessageType.GAME_STARTED,
                    updatedRoom,
                    updatedRoom.getId(),
                    gameMessage.getPlayerId());
            messagingTemplate.convertAndSend("/topic/room/" + updatedRoom.getId(), response);
        }
    }
}
