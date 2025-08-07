package com.type.multi_typer.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    private String id;
    private String code;
    @Enumerated(EnumType.STRING)
    private GameState gameState;
    @Column(length = 2000)
    private String text;

    @OneToOne(cascade = CascadeType.ALL)
    private Player createdBy;
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Player> players;

    private LocalDateTime createdAt;
    private LocalDateTime gameStartedAt;
    private LocalDateTime gameEndedAt;

    private LocalDateTime lastActivityAt;

    private int maxPlayers;


    public Room() {
        this.players = new ArrayList<>();
    }

    public Room(String id, String code, String text, int maxPlayers, String creatorName) {
        this.id = id;
        this.code = code;
        this.text = text;
        this.gameState = GameState.WAITING;
        this.players = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.lastActivityAt = LocalDateTime.now();
        this.maxPlayers = maxPlayers;
        this.createdBy = new Player(creatorName);
        this.createdBy.setRoom(this);
        addPlayer(this.createdBy);
    }

    public void updateActivity() {
        this.lastActivityAt = LocalDateTime.now();
    }

    public void addPlayer(Player player) {
        this.players.add(player);
        player.setRoom(this);
        updateActivity();
    }

    public void removePlayer(Player player) {
        players.remove(player);
        player.setRoom(null);
        updateActivity();
    }

    public Player getPlayer(String playerId) {
        return this.players.stream()
                .filter(player -> player.getId().equals(playerId))
                .findFirst()
                .orElse(null);
    }

    public LocalDateTime getLastActivityAt() {
        return lastActivityAt;
    }

    public void setLastActivityAt(LocalDateTime lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    public boolean isFull() {
        return this.players.size() >= this.maxPlayers;
    }

    public boolean canStart() {
        return this.players.size() > 1 && this.gameState == GameState.WAITING;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getGameStartedAt() {
        return gameStartedAt;
    }

    public void setGameStartedAt(LocalDateTime gameStartedAt) {
        this.gameStartedAt = gameStartedAt;
    }

    public LocalDateTime getGameEndedAt() {
        return gameEndedAt;
    }

    public void setGameEndedAt(LocalDateTime gameEndedAt) {
        this.gameEndedAt = gameEndedAt;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Player getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Player createdBy) {
        this.createdBy = createdBy;
    }
}
