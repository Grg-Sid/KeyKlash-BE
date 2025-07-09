package com.type.multi_typer.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Room {
    private String id;
    private String code;
    private GameState gameState;
    private String text;
    private Player createdBy;
    private List<Player> players;
    private LocalDateTime createdAt;
    private LocalDateTime gameStartedAt;
    private LocalDateTime gameEndedAt;
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
        this.maxPlayers = maxPlayers;
        this.createdBy = new Player(creatorName, id);
        System.out.println(this.createdBy.getNickname());
        addPlayer(this.createdBy);
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void removePlayer(String playerId) {
        this.players.removeIf(player -> player.getId().equals(playerId));
    }

    public Player getPlayer(String playerId) {
        return this.players.stream()
                .filter(player -> player.getId().equals(playerId))
                .findFirst()
                .orElse(null);
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
