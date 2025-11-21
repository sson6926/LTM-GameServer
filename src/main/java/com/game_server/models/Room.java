package com.game_server.models;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Room {
    private String roomCode;
    private User host;
    private List<User> players;
    private int maxPlayers;
    private String status; // WAITING, READY, PLAYING
    private long createdAt;

    public Room(User host) {
        this.roomCode = generateRoomCode();
        this.host = host;
        this.players = new ArrayList<>();
        this.players.add(host);
        this.maxPlayers = 2;
        this.status = "WAITING";
        this.createdAt = System.currentTimeMillis();
    }

    private String generateRoomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Tạo mã 6 số
        return String.valueOf(code);
    }

    public boolean addPlayer(User user) {
        if (players.size() < maxPlayers && !status.equals("PLAYING")) {
            players.add(user);
            if (players.size() == maxPlayers) {
                status = "READY";
            }
            return true;
        }
        return false;
    }

    public void removePlayer(User user) {
        players.remove(user);
        if (players.isEmpty()) {
            status = "CLOSED";
        } else if (players.size() < maxPlayers) {
            status = "WAITING";
        }
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("roomCode", roomCode);
        json.put("host", host.toJson());
        json.put("playerCount", players.size());
        json.put("maxPlayers", maxPlayers);
        json.put("status", status);
        return json;
    }

    // Getters and Setters
    public String getRoomCode() { return roomCode; }
    public User getHost() { return host; }
    public List<User> getPlayers() { return players; }
    public int getMaxPlayers() { return maxPlayers; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getCreatedAt() { return createdAt; }
}