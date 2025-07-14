package com.type.multi_typer.service;

import com.type.multi_typer.dto.GameMessage;
import com.type.multi_typer.dto.MessageType;
import com.type.multi_typer.dto.QuoteResponse;
import com.type.multi_typer.dto.TypingUpdate;
import com.type.multi_typer.model.GameState;
import com.type.multi_typer.model.Player;
import com.type.multi_typer.model.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, String> roomCodes = new ConcurrentHashMap<>();
    private final Random random = new Random();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Room createRoom(int maxPlayer, String creatorName) {
        String roomId = UUID.randomUUID().toString().substring(0, 6);
        String roomCode = generateRoomCode();
        String text = generateRandomQuote();

        Room room = new Room(roomId, roomCode, text, maxPlayer, creatorName);
        rooms.put(roomId, room);
        roomCodes.put(roomCode, roomId);
        return room;
    }

    public Player joinRoom(String roomCode, String nickname) {
        logger.info("Joining room: {}, {}", roomCode, nickname);
        String roomId = roomCodes.get(roomCode.toUpperCase());
        if (roomId == null) {
            throw new IllegalArgumentException("Room code " + roomCode + " not found");
        }

        Room room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Room code " + roomCode + " not found");
        }
        if (room.isFull()) {
            throw new IllegalArgumentException("Room code " + roomCode + " is already full");
        }
        if (room.getGameState() != GameState.WAITING) {
            throw new IllegalArgumentException("Room code " + roomCode + " is already in progress");
        }

        Player newPlayer = new Player(nickname, roomId);
        room.addPlayer(newPlayer);

        broadcastRoomUpdate(room);
        return newPlayer;
    }

    public void playerReady(String roomCode, String playerId) {
        Room room = rooms.get(roomCode);
        if (room == null) {
            throw new IllegalArgumentException("Room code " + roomCode + " not found");
        }

        Player player = room.getPlayer(playerId);
        if (player == null) {
            throw new IllegalArgumentException("Player ID " + playerId + " not found");
        }

        player.setReady(true);

        // Check if all players are ready
        if (room.getPlayers().stream().allMatch(Player::getReady)) {
            startGame(room.getId());
        }
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public Room getRoomByCode(String roomCode) {
        String roomId = roomCodes.get(roomCode.toUpperCase());
        return roomId != null ? rooms.get(roomId) : null;
    }

    public void leaveRoom(String roomId, String playerId) {
        Room room = rooms.get(roomId);
        if (room != null) {
            room.removePlayer(playerId);

            if (room.getPlayers().isEmpty()) {
                rooms.remove(roomId);
                roomCodes.remove(room.getCode());
            }

            broadcastRoomUpdate(room);
        }
    }

    public void startGame(String roomId) {
        Room room = rooms.get(roomId);
        if (room != null && room.canStart()) {
            room.setGameState(GameState.IN_PROGRESS);
            room.setGameStartedAt(LocalDateTime.now());
            GameMessage message = new GameMessage(MessageType.GAME_STARTED, room, room.getId(), null);
            messagingTemplate.convertAndSend("/topic/room/" + room.getId(), message);
        }
    }

    public void updatePlayerProgress(String roomId, String playerId, TypingUpdate typingUpdate) {
//        TODO: might add locks in future
        //            TODO: Implement redis/db
        Room room = rooms.get(roomId);
        if (room == null || room.getPlayers().isEmpty() || room.getGameState() != GameState.IN_PROGRESS) {
            logger.warn("Room {} is not in progress", roomId);
        }

        assert room != null;
        Player player = room.getPlayer(playerId);

        if (player == null) {
            logger.warn("Player with id {} not found", playerId);
            return;
        }

        if (typingUpdate.getWpm() > 300) {
            logger.warn("Player {} has submitted unusually high WPM: {}", playerId, typingUpdate.getWpm());
            player.setWpm(300);
        } else {
            player.setWpm(typingUpdate.getWpm());
        }

        if (typingUpdate.getAccuracy() < 0 || typingUpdate.getAccuracy() > 100) {
            logger.warn("Player {} has submitted invalid accuracy: {}", playerId, typingUpdate.getAccuracy());
            player.setAccuracy(100);
        } else {
            player.setAccuracy(typingUpdate.getAccuracy());
        }

        if (typingUpdate.getCurrentPosition() < 0 || typingUpdate.getCurrentPosition() > room.getText().length()) {
            logger.warn("Player {} has submitted invalid position: {}", playerId, typingUpdate.getCurrentPosition());
            player.setCurrentPosition(room.getText().length());
        } else {
            player.setCurrentPosition(typingUpdate.getCurrentPosition());
        }

        return;
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    private String generateRoomCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString();
    }

    public String generateRandomQuote() {
        String quote = "Be Impeccable with Your Word. Speak with integrity. Say only what you mean. Avoid using the word to speak against yourself or to gossip about others. Use the power of your word in the direction of truth and love.";
        String url = "https://api.quotable.kurokeita.dev/api/quotes/random?minLength=200&maxLength=400";
        try {
            QuoteResponse quoteResponse = restTemplate.getForObject(url, QuoteResponse.class);
            if (quoteResponse == null) {
                return quote;
            }
            quote = quoteResponse.getQuote().getContent();
        } catch (Exception e) {
            logger.error("Exception occurred while generating quote {}",quote, e);
        }
        return quote;
    }

    public void broadcastRoomUpdate(Room room) {
        if (room == null) return;

        GameMessage message = new GameMessage(MessageType.ROOM_UPDATE, room, room.getId(), null);
        messagingTemplate.convertAndSend("/topic/room/" + room.getId(), message);
        logger.info("Broadcast update for room {}", room.getId());
    }
}
