package com.game_server.models;

import java.time.LocalDateTime;

public class RoundDetail {
    private int id;
    private int score;
    private int is_winner;
    private int round_id;
    private int user_id;

    public RoundDetail(int id, int score, int is_winner, int round_id, int user_id) {
        this.id = id;
        this.score = score;
        this.is_winner = is_winner;
        this.round_id = round_id;
        this.user_id = user_id;
    }
    public RoundDetail( int score, int is_winner, int round_id, int user_id) {
        this.score = score;
        this.is_winner = is_winner;
        this.round_id = round_id;
        this.user_id = user_id;
    }
    public RoundDetail( int round_id, int user_id) {
        this.id = id;
        this.round_id = round_id;
        this.user_id = user_id;
    }
    public RoundDetail() {
    }
    public int getId() {
        return id;
    }
    public int getScore() {
        return score;
    }
    public int getIs_winner() {
        return is_winner;
    }
    public int getRound_id() {
        return round_id;
    }
    public int getUser_id() {
        return user_id;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public void setIs_winner(int is_winner) {
        this.is_winner = is_winner;
    }
    public void setRound_id(int round_id) {
        this.round_id = round_id;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public void setId(int id) {
        this.id = id;
    }


    
}
