package com.game_server.models;

public class UserAnswer {
    private int id;
    private int timeCompleted;
    private String status;
    private int roundDetailId;

    public UserAnswer(int id, int timeCompleted, String status, int roundDetailId) {
        this.id = id;
        this.timeCompleted = timeCompleted;
        this.status = status;
        this.roundDetailId = roundDetailId;
    }
 
    
    public UserAnswer() {
    }
    public UserAnswer(int timeCompleted, String status, int roundDetailId) {
        this.timeCompleted = timeCompleted;
        this.status = status;
        this.roundDetailId = roundDetailId;
    }
    public int getId() {
        return id;
    }
    public int getTimeCompleted() {
        return timeCompleted;
    }
    public String getStatus() {
        return status;
    }
    public int getRoundDetailId() {
        return roundDetailId;
    }
    public void setTimeCompleted(int timeCompleted) {
        this.timeCompleted = timeCompleted;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setRoundDetailId(int roundDetailId) {
        this.roundDetailId = roundDetailId;
    }
    public void setId(int id) {
        this.id = id;
    }
    

}
