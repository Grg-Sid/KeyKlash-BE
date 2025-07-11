package com.type.multi_typer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.type.multi_typer.dto.RoomCreateRequest;
import com.type.multi_typer.dto.RoomJoinRequest;
import com.type.multi_typer.model.Player;
import com.type.multi_typer.model.Room;
import com.type.multi_typer.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/hello-world")
    public ResponseEntity<String> helloWorld() {
        System.out.println("hello-world");
        return ResponseEntity.ok("Hello World");
    }

    @PostMapping("/test")
    public ResponseEntity<String> testPost() {
        return ResponseEntity.ok("POST Success");
    }

    @PostMapping("/room/create")
    public ResponseEntity<Room> createRoom(@RequestBody RoomCreateRequest request) {
        try {
            int maxPlayers = request.getMaxPlayers() > 0 ? request.getMaxPlayers() : 5;
            String creatorName = request.getCreatorName();
            Room room = gameService.createRoom(maxPlayers, creatorName);
            return ResponseEntity.ok(room);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/room/join")
    public ResponseEntity<Player> joinRoom(@RequestBody RoomJoinRequest request) {
        try {
            Player newPlayer = gameService.joinRoom(request.getRoomCode(), request.getNickname());
            return ResponseEntity.ok(newPlayer);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
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

