package com.type.multi_typer.controller;

import com.type.multi_typer.dto.RoomCreateRequest;
import com.type.multi_typer.dto.RoomJoinRequest;
import com.type.multi_typer.dto.RoomRestartRequest;
import com.type.multi_typer.model.Player;
import com.type.multi_typer.model.Room;
import com.type.multi_typer.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GameController {

    private final Logger logger = LoggerFactory.getLogger(GameController.class);

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/hello-world")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello World");
    }

    @PostMapping("/test")
    public ResponseEntity<String> testPost() {
        return ResponseEntity.ok("POST Success");
    }

    @PostMapping("/room/create")
    public ResponseEntity<Room> createRoom(@RequestBody RoomCreateRequest request) {
        try {
            String creatorName = request.getCreatorName();
            String text = request.getText();
            Room room = gameService.createRoom(creatorName, text);
            return ResponseEntity.ok(room);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/room/restart")
    public ResponseEntity<Room> restartRoom(@RequestBody RoomRestartRequest request) {
        try {
            String roomCode = request.getRoomCode();
            String newText = request.getNewText();
            Room room = gameService.restartRoom(roomCode, newText);
            return ResponseEntity.ok(room);
        } catch (Exception e) {
            logger.error("Error restarting room", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/room/join")
    public ResponseEntity<Player> joinRoom(@RequestBody RoomJoinRequest request) {
        try {
            Player newPlayer = gameService.joinRoom(request.getRoomCode(), request.getNickname());
            return ResponseEntity.ok(newPlayer);
        } catch (IllegalArgumentException e) {
            logger.error("Exception occurred while joining room", e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<Room> getRoom(@PathVariable String roomId) {
        Room room = gameService.getRoom(roomId);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(room);
    }

    @GetMapping("/room/code/{roomCode}")
    public ResponseEntity<Room> getRoomByCode(@PathVariable String roomCode) {
        Room room = gameService.getRoomByCode(roomCode);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(room);
    }

    @GetMapping("/room/{roomCode}/{playerId}")
    public ResponseEntity<String> markPlayerAsReady(@PathVariable String roomCode, @PathVariable String playerId) {
        try {
            gameService.playerReady(roomCode, playerId);
            return ResponseEntity.ok("Player ready");
        } catch (IllegalArgumentException e) {
            logger.error("Exception occurred while marking player", e);
            return ResponseEntity.badRequest().build();
        }
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

    @GetMapping("/rooms")
    public ResponseEntity<List<Room>> getRooms() {
        return ResponseEntity.ok((gameService.getAllRooms()));
    }
}

