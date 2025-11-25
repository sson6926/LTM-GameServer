package com.game_server.models;

import java.time.LocalDateTime;

public class MatchHistory {
    private int matchId;
    private String opponentUsername;
    private String opponentNickname;
    private String result;        // "WIN" hoặc "LOSS"
    private int userTotalScore;   // Tổng điểm của user trong match
    private int opponentTotalScore;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int roundCount;       // Số round đã chơi

    public MatchHistory(int matchId, String opponentUsername, String opponentNickname,
                        String result, int userTotalScore, int opponentTotalScore,
                        LocalDateTime startTime, LocalDateTime endTime, int roundCount) {
        this.matchId = matchId;
        this.opponentUsername = opponentUsername;
        this.opponentNickname = opponentNickname;
        this.result = result;
        this.userTotalScore = userTotalScore;
        this.opponentTotalScore = opponentTotalScore;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roundCount = roundCount;
    }

    // Getters
    public int getMatchId() { return matchId; }
    public String getOpponentUsername() { return opponentUsername; }
    public String getOpponentNickname() { return opponentNickname; }
    public String getResult() { return result; }
    public int getUserTotalScore() { return userTotalScore; }
    public int getOpponentTotalScore() { return opponentTotalScore; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public int getRoundCount() { return roundCount; }
}
