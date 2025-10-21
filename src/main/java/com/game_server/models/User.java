package com.game_server.models;

import org.json.JSONObject;

public class User {
    private int id;
    private String username;
    private String password;
    private String nickname;
    private int totalMatches;
    private int totalWins;
    private int totalScore;

    public User(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(int id, String username, String password, String nickname, int totalMatches, int totalWins, int totalScore) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.totalMatches = totalMatches;
        this.totalWins = totalWins;
        this.totalScore = totalScore;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("username", username);
        json.put("nickname", nickname);
        json.put("total_matches", totalMatches);
        json.put("total_wins", totalWins);
        json.put("total_score", totalScore);
        return json;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getTotalMatches() {
        return totalMatches;
    }

    public void setTotalMatches(int totalMatches) {
        this.totalMatches = totalMatches;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(int totalWins) {
        this.totalWins = totalWins;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
}