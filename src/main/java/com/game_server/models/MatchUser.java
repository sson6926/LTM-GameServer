/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.game_server.models;

/**
 *
 * @author ADMIN
 */
public class MatchUser {

    private int id;
    private Match match;
    private User user;
    private int score;
    private boolean isWinner;

    public MatchUser() {
    }

    public MatchUser(Match match, User user) {
        this.match = match;
        this.user = user;
        this.score = 0;
        this.isWinner = false;
    }

    public MatchUser(Match match, User user, int score, boolean isWinner) {
        this.match = match;
        this.user = user;
        this.score = score;
        this.isWinner = isWinner;
    }
    public MatchUser(int id, Match match, User user, int score, boolean isWinner) {
        this.id = id;
        this.match = match;
        this.user = user;
        this.score = score;
        this.isWinner = isWinner;
    }
    public int getId() {
        return id;
    }
    
    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public void setWinner(boolean winner) {
        isWinner = winner;
    }
    
    @Override
    public String toString() {
        return "MatchUser{"
                + "match=" + (match != null ? match.getId() : "null")
                + ", user=" + (user != null ? user.getUsername() : "null")
                + ", score=" + score
                + ", isWinner=" + isWinner
                + '}';
    }
}
