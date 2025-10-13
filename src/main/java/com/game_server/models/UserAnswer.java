package com.game_server.models;

public class UserAnswer {
    private int id;
    private int userId;
    private int matchId;
    private int round_number;
    private int timeCompleted; // in seconds
    private String status; // e.g., "COMPLETED", "FAILED"

    public UserAnswer(int id, int userId, int matchId, int round_number, int timeCompleted, String status) {
        this.id = id;
        this.userId = userId;
        this.matchId = matchId;
        this.round_number = round_number;
        this.timeCompleted = timeCompleted;
        this.status = status;
    }
    public UserAnswer(int userId, int matchId, int round_number, int timeCompleted, String status) {
        this.userId = userId;
        this.matchId = matchId;
        this.round_number = round_number;
        this.timeCompleted = timeCompleted;
        this.status = status;
    }
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getMatchId() {
        return matchId;
    }

    public int getRoundNumber() {
        return round_number;
    }

    public int getTimeCompleted() {
        return timeCompleted;
    }

    public String getStatus() {
        return status;
    
    }

    
}
