package com.type.multi_typer.service;

import com.type.multi_typer.dto.QuoteResponse;
import com.type.multi_typer.dto.TypingUpdate;
import com.type.multi_typer.model.GameState;
import com.type.multi_typer.model.Player;
import com.type.multi_typer.model.Room;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, String> roomCodes = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public Room createRoom(int maxPlayer) {
        String roomId = UUID.randomUUID().toString();
        String roomCode = generateRoomCode();
        String text = generateRandomQuote();

        Room room = new Room(roomId, roomCode, text);
        rooms.put(roomId, room);
        roomCodes.put(roomCode, roomId);
        return room;
    }

    public Room joinRoom(String roomCode, String nickname) {
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

        return room;
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
        }
    }

    public void startGame(String roomId) {
        Room room = rooms.get(roomId);
        if (room != null && room.canStart()) {
            room.setGameState(GameState.IN_PROGRESS);
            room.setGameStartedAt(LocalDateTime.now());
        }
    }

    public void updatePlayerProgress(String roomId, String playerId, TypingUpdate update) {
        Room room = rooms.get(roomId);
        if (room == null || room.getGameState() != GameState.IN_PROGRESS) {
            return;
        }

        Player player = room.getPlayer(playerId);
        if (player == null) {
            return;
        }

        String originalText = room.getText();
        String typedText = update.getTypedText();

        double accuracy = calculateAccuracy(originalText, typedText);
        int wpm = calculateWPM(typedText, room.getGameStartedAt());

        player.setCurrentPosition(update.getCurrentPosition());
        player.setAccuracy(accuracy);
        player.setWpm(wpm);

        if (update.getCurrentPosition() >= originalText.length()) {
            player.setFinished(true);
            player.setFinishedAt(LocalDateTime.now());

            boolean allFinished = room.getPlayers().stream().allMatch(Player::isFinished);
            if (allFinished) {
                room.setGameState(GameState.FINISHED);
                room.setGameEndedAt(LocalDateTime.now());
            }
        }
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

    private double calculateAccuracy(String originalText, String typedText) {
        if (typedText.isEmpty()) return 100.0;

        int correct = 0;
        int minLength = Math.min(originalText.length(), typedText.length());
        for (int i = 0; i < minLength; i++) {
            if (originalText.charAt(i) == typedText.charAt(i)) {
                correct++;
            }
        }

        return (double) correct / typedText.length();
    }

    private int calculateWPM(String typedText, LocalDateTime time) {
        long minutes = ChronoUnit.SECONDS.between(time, LocalDateTime.now()) / 60.0 > 0
                ? ChronoUnit.SECONDS.between(time, LocalDateTime.now()) / 60
                : 1;

        int words = typedText.length() / 5;
        return Math.max(1, (int) (words / Math.max(1, minutes)));
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
            System.out.println(e.getMessage());
        }
        return quote;
    }
}
