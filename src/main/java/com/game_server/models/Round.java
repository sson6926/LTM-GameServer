package com.game_server.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Round {
    private int id;
    private int matchId;
    private LocalDateTime createdAt;

    public Round(int id, int matchId, LocalDateTime createdAt) {
        this.id = id;
        this.matchId = matchId;
        this.createdAt = createdAt;
    }
    public int getId() {
        return id;
    }
    public int getMatchId() {
        return matchId;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    

}
