/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.game_server.models;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class Match {

    private int id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private boolean isVisible;
    
    private List<MatchUser> matchUsers;

    
    public Match() {}

    public Match(LocalDateTime startTime, String status, boolean isVisible) {
        this.startTime = startTime;
        this.status = status;
        this.isVisible = isVisible;
    }

    public Match(int id, LocalDateTime startTime, LocalDateTime endTime,
            String status, boolean isVisible) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.isVisible = isVisible;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public List<MatchUser> getMatchUsers() {
        return matchUsers;
    }

    public void setMatchUsers(List<MatchUser> matchUsers) {
        this.matchUsers = matchUsers;
    }

    @Override
    public String toString() {
        return "Match{"
                + "id=" + id
                + ", startTime=" + startTime
                + ", endTime=" + endTime
                + ", status='" + status + '\''
                + ", isVisible=" + isVisible
                + '}';
    }
}
