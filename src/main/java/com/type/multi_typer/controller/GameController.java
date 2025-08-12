package com.type.multi_typer.controller;

import com.type.multi_typer.dto.RoomCreateRequest;
import com.type.multi_typer.dto.RoomJoinRequest;
import com.type.multi_typer.dto.RoomRestartRequest;
import com.type.multi_typer.model.Player;
import com.type.multi_typer.model.Room;
import com.type.multi_typer.service.GameService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class GameController {

    private final Logger logger = LoggerFactory.getLogger(GameController.class);
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Running");
    }


    @PostMapping("/room/create")
    public ResponseEntity<Room> createRoom(@RequestBody RoomCreateRequest request) {
        try {
            String text = request.getText();
            if (text == null || text.isEmpty()) {
                logger.error("Text Can't be null or empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            Room room = gameService.createRoom(request.getCreatorName(), request.getText());
            return ResponseEntity.ok(room);
        } catch (Exception e) {
            logger.error("Error creating room", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/room/restart")
    public ResponseEntity<Room> restartRoom(@Valid @RequestBody RoomRestartRequest request) {
        try {
            gameService.resetRoom(request.getRoomId(), request.getNewText());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error restarting room", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/room/join")
    public ResponseEntity<Player> joinRoom(@Valid @RequestBody RoomJoinRequest request) {
        try {
            Player newPlayer = gameService.joinRoom(request.getRoomCode(), request.getNickname());
            return ResponseEntity.ok(newPlayer);
        } catch (IllegalArgumentException e) {
            logger.error("Exception occurred while joining room", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalStateException e) {
            logger.warn("Exception occurred while joining room", e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @PostMapping("/room/{roomId}/leave/{playerId}")
    public ResponseEntity<Void> leaveRoom(@PathVariable String roomId, @PathVariable String playerId) {
        gameService.leaveRoom(roomId, playerId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/room/{roomCode}")
    public ResponseEntity<Room> getRoom(@PathVariable String roomCode) {
        Room room = gameService.getRoomByCode(roomCode);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(room);
    }

    @GetMapping("/room/{roomId}/start")
    public ResponseEntity<Room> startRoom(@PathVariable String roomId) {
        try {
            gameService.startGame(roomId);
            return ResponseEntity.ok(gameService.getRoom(roomId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

