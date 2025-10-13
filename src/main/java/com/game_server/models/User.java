package com.game_server.models;

public class User {
    private int id;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private int score;
    private int wins;
    private int totalMatches;
    private boolean isOnline;
    private boolean isPlaying;


    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public User(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }
    
    public User(String username, String password, String nickname,
                boolean isOnline, boolean isPlaying) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.isOnline = isOnline;
        this.isPlaying = isPlaying;
    }
    public User(int id, String username, String password, String nickname,
                boolean isOnline, boolean isPlaying) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.isOnline = isOnline;
        this.isPlaying = isPlaying;
    }
    
    public User(int id, String username, String nickname) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.password = "";
        this.isOnline = false;
        this.isPlaying = false;
    }
    public User(int id, String username, String nickname, String avatar, int score, int wins, int totalMatches) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.avatar = avatar;
        this.score = score;
        this.wins = wins;
        this.totalMatches = totalMatches;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public int getScore() {
        return score;
    }

    public int getWins() {
        return wins;
    }

    public int getTotalMatches() {
        return totalMatches;
    }
    public void setScore(int score) {
        this.score = score;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void setTotalMatches(int totalMatches) {
        this.totalMatches = totalMatches;
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

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}