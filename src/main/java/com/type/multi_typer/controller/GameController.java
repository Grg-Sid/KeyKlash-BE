package com.type.multi_typer.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.type.multi_typer.dto.RoomCreateRequest;
import com.type.multi_typer.dto.RoomJoinRequest;
import com.type.multi_typer.model.Room;
import com.type.multi_typer.service.GameService;
import com.type.multi_typer.sse.RoomSseManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GameController {

    private final RoomSseManager roomSseManager;
    private final GameService gameService;
    private final ObjectMapper objectMapper;

    public GameController(GameService gameService, RoomSseManager roomSseManager, ObjectMapper objectMapper) {
        this.gameService = gameService;
        this.roomSseManager = roomSseManager;
        this.objectMapper = objectMapper;
    }

    @GetMapping("room/{roomCode}/stream")
    public SseEmitter stream(@PathVariable String roomCode) {
        SseEmitter emitter = roomSseManager.addEmitter(roomCode);
        try {
            emitter.send(SseEmitter.event().name("connection").data("SSE connection established for room " + roomCode));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return emitter;
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
    public ResponseEntity<Room> joinRoom(@RequestBody RoomJoinRequest request) {
        try {
            Room updatedRoom = gameService.joinRoom(request.getRoomCode(), request.getNickname());
            String roomJson = objectMapper.writeValueAsString(updatedRoom);

            roomSseManager.sendEvent(request.getRoomCode(), roomJson);
            System.out.println("Sent SSE update to room: " + request.getRoomCode());

            return ResponseEntity.ok(updatedRoom);

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (JsonProcessingException e) {
            System.err.println("Error serializing room to JSON: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
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

