package com.type.multi_typer.service;

import com.type.multi_typer.dto.GameMessage;
import com.type.multi_typer.dto.MessageType;
import com.type.multi_typer.dto.TypingUpdate;
import com.type.multi_typer.model.GameState;
import com.type.multi_typer.model.Player;
import com.type.multi_typer.model.Room;
import com.type.multi_typer.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class GameService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RoomRepository roomRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final Random random = new Random();

    public GameService(RoomRepository roomRepository, RedisTemplate<String, Object> redisTemplate, SimpMessagingTemplate messagingTemplate) {
        this.roomRepository = roomRepository;
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public Room createRoom(String creatorName, String text) {
        String roomId = UUID.randomUUID().toString().substring(0, 6);
        String roomCode = generateRoomCode();

        Room room = new Room(roomId, roomCode, text, 6, creatorName);
        logger.info("Creating new room with code {}", roomCode);
        return roomRepository.save(room);
    }

    @Transactional
    public void resetRoom(String roomId, String newText) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found for reset: " + roomId));

        room.setText(newText);
        room.setGameState(GameState.IN_PROGRESS);
        room.setGameStartedAt(LocalDateTime.now());
        room.getPlayers().forEach(player -> {
            player.setFinished(false);
            player.setCurrentPosition(0);});
        room.updateActivity();
        roomRepository.save(room);
        logger.info("Room {} has been reset", roomId);

        GameMessage restartMessage = new GameMessage(
                MessageType.GAME_RESTART,
                room,
                room.getId(),
                room.getCreatedBy().getId()
        );
        messagingTemplate.convertAndSend("/topic/room/" + roomId, restartMessage);
    }

    @Transactional
    public Player joinRoom(String roomCode, String nickname) {
        Room room = roomRepository.findRoomByCode(roomCode.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        logger.info("Joining room: {}, {}", roomCode, nickname);

        if (room.isFull()) {
            throw new IllegalArgumentException("Room code " + roomCode + " is already full");
        }
        if (room.getGameState() != GameState.WAITING) {
            throw new IllegalArgumentException("Room code " + roomCode + " is already in progress");
        }

        Player newPlayer = new Player(nickname);
        room.addPlayer(newPlayer);
        roomRepository.save(room);
        broadcastRoomUpdate(room);
        return newPlayer;
    }

    @Transactional
    public void leaveRoom(String roomId, String playerId) {
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room != null) {
            Player player = room.getPlayer(playerId);
            if (player != null) {
                room.removePlayer(player);
                logger.info("Player {} left room {}", playerId, roomId);

                if (room.getPlayers().isEmpty()) {
                    logger.info("Room {} is empty, deleting.", roomId);
                } else {
                    room.updateActivity();
                    roomRepository.save(room);
                    broadcastRoomUpdate(room);
                }
            }
        }

    }

    public Room getRoom(String roomId) {
        return roomRepository.findById(roomId).orElse(null);
    }

    public Room getRoomByCode(String roomCode) {
        return roomRepository.findRoomByCode(roomCode.toUpperCase()).orElseThrow(() -> new IllegalArgumentException("Room not found"));
    }

    public void updatePlayerProgressInCache(TypingUpdate typingUpdate) {
        String roomId = typingUpdate.getRoomId();
        String playerId = typingUpdate.getPlayerId();
        int position = typingUpdate.getCurrentPosition();

        String redisKey = "game_progress:" + roomId;
        redisTemplate.opsForHash().put(redisKey, playerId, position);
        redisTemplate.expire(redisKey, 1, TimeUnit.HOURS);
    }

    @Transactional
    public void persistPlayerFinished(String roomId, String playerId) {
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room != null) {
            Player player = room.getPlayer(playerId);
            if (player != null && !player.isFinished()) {
                player.setFinished(true);
                room.updateActivity();
                roomRepository.save(room);
                logger.info("Player {} finished room {}", playerId, roomId);
                GameMessage finishMessage = new GameMessage(
                        MessageType.PLAYER_FINISHED,
                        player,
                        roomId,
                        playerId
                );
                messagingTemplate.convertAndSend("/topic/room/" + roomId, finishMessage);
            }
        }
    }

    @Transactional
    public void startGame(String roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Room not found"));
        if (room.canStart()) {
            room.setGameState(GameState.IN_PROGRESS);
            room.setGameStartedAt(LocalDateTime.now());
            room.updateActivity();
            roomRepository.save(room);
            logger.info("Room {} has been started", roomId);
//            GameMessage message = new GameMessage(
//                    MessageType.GAME_STARTED,
//                    room,
//                    room.getId(),
//                    null);
//            messagingTemplate.convertAndSend("/topic/room/" + room.getId(), message);
        } else {
            logger.warn("Could not start game in room {}", roomId);
        }
    }

    @Transactional
    public void gameOver(String roomId) {
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room != null) {
            room.setGameState(GameState.FINISHED);
            room.setGameEndedAt(LocalDateTime.now());
            room.updateActivity();
            roomRepository.save(room);
            logger.info("Room {} has been finished", roomId);
            GameMessage gameOverMessage = new GameMessage(
                    MessageType.GAME_OVER,
                    room,
                    roomId,
                    null
            );
            messagingTemplate.convertAndSend("/topic/room/" + roomId, gameOverMessage);
        }
    }

    @Transactional(readOnly = true)
    public boolean hasAllPlayerFinished(String roomId) {
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null || room.getPlayers().isEmpty()) {
            System.out.println("Get Player is Empty");
            return false;
        }

        return room.getPlayers().stream().allMatch(Player::isFinished);
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

    public void broadcastRoomUpdate(Room room) {
        if (room == null) return;

        GameMessage message = new GameMessage(MessageType.ROOM_UPDATE, room, room.getId(), null);
        messagingTemplate.convertAndSend("/topic/room/" + room.getId(), message);
        logger.info("Broadcast update for room {}", room.getId());
    }

    @Scheduled(fixedRate = 1800000)
    @Transactional
    public void cleanupInactiveRooms() {
        logger.info("Cleaning up inactive rooms");
        LocalDateTime cutoff = LocalDateTime.now().minusHours(1);
        List<Room> inactiveRooms = roomRepository.findByLastActivityAtBefore(cutoff);

        if (!inactiveRooms.isEmpty()) {
            logger.info("Cleaning up inactive rooms {}", inactiveRooms.size());
            roomRepository.deleteAll(inactiveRooms);
        } else {
            logger.info("No inactive rooms found");
        }
    }
}
