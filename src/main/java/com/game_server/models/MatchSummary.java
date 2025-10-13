package com.game_server.models;

public class MatchSummary {
    private int matchId;
    private int user1TotalScore;
    private int user2TotalScore;
    private int user1Wins;
    private int user2Wins;
    private int totalRounds;
    
    public MatchSummary() {}
    public MatchSummary(int matchId, int user1TotalScore, int user2TotalScore,
            int user1Wins, int user2Wins, int totalRounds) {
        this.matchId = matchId;
        this.user1TotalScore = user1TotalScore;
        this.user2TotalScore = user2TotalScore;
        this.user1Wins = user1Wins;
        this.user2Wins = user2Wins;
        this.totalRounds = totalRounds;
    }
    
    // Getters and Setters
    public int getMatchId() { return matchId; }
    public void setMatchId(int matchId) { this.matchId = matchId; }
    
    public int getUser1TotalScore() { return user1TotalScore; }
    public void setUser1TotalScore(int user1TotalScore) { this.user1TotalScore = user1TotalScore; }
    
    public int getUser2TotalScore() { return user2TotalScore; }
    public void setUser2TotalScore(int user2TotalScore) { this.user2TotalScore = user2TotalScore; }
    
    public int getUser1Wins() { return user1Wins; }
    public void setUser1Wins(int user1Wins) { this.user1Wins = user1Wins; }
    
    public int getUser2Wins() { return user2Wins; }
    public void setUser2Wins(int user2Wins) { this.user2Wins = user2Wins; }
    
    public int getTotalRounds() { return totalRounds; }
    public void setTotalRounds(int totalRounds) { this.totalRounds = totalRounds; }
}
