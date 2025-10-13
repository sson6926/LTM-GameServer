/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.game_server.dao;

import com.game_server.models.Match;
import com.game_server.models.MatchSummary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 *
 * @author ADMIN
 */
public class MatchDAO extends DAO{
    
    public MatchDAO() {
        super();
    }
    
    public int save(Match match) {
        String sql = "INSERT INTO `match` (start_time, end_time, `status`, is_visible) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setTimestamp(1, Timestamp.valueOf(match.getStartTime()));
            ps.setTimestamp(2, match.getEndTime() != null ? Timestamp.valueOf(match.getEndTime()) : null);
            ps.setString(3, match.getStatus());
            ps.setBoolean(4, match.isVisible());
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int matchId = generatedKeys.getInt(1);
                        match.setId(matchId);
                        return matchId;
                    }
                }
            }
            
            System.out.println(">>>>>>>>>>>>>>>Helloo");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Lỗi
    }
    
    public Match getById(int matchId) {
        String sql = "SELECT id, start_time, end_time, `status`, is_visible FROM `match` WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, matchId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Match(
                        rs.getInt("id"),
                        // Chuyển Timestamp → LocalDateTime
                        rs.getTimestamp("start_time").toLocalDateTime(),
                        rs.getTimestamp("end_time") != null ? rs.getTimestamp("end_time").toLocalDateTime() : null,
                        rs.getString("status"),
                        rs.getBoolean("is_visible")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean updateEndTime(int matchId, LocalDateTime endTime) {
        String sql = "UPDATE `match` SET end_time = ?, `status` = 'FINISHED' WHERE id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(endTime));
            ps.setInt(2, matchId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public MatchSummary getMatchSummary(int matchId, int user1Id, int user2Id) {
        MatchSummary summary = new MatchSummary();
        summary.setMatchId(matchId);

        // ✅ Lấy tổng điểm và số win từ bảng match_user
        String sql = "SELECT user_id, SUM(score) as total_score, SUM(is_winner) as total_wins " +
                    "FROM match_user WHERE match_id = ? GROUP BY user_id";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, matchId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                int totalScore = rs.getInt("total_score");
                int totalWins = rs.getInt("total_wins");
                
                if (userId == user1Id) {
                    summary.setUser1TotalScore(totalScore);
                    summary.setUser1Wins(totalWins);
                } else if (userId == user2Id) {
                    summary.setUser2TotalScore(totalScore);
                    summary.setUser2Wins(totalWins);
                }
            }
            
            // ✅ Đếm tổng số round từ bảng user_answers
            String roundSql = "SELECT COUNT(DISTINCT round_number) as total_rounds FROM user_answers WHERE match_id = ?";
            try (PreparedStatement roundStmt = connection.prepareStatement(roundSql)) {
                roundStmt.setInt(1, matchId);
                ResultSet roundRs = roundStmt.executeQuery();
                if (roundRs.next()) {
                    summary.setTotalRounds(roundRs.getInt("total_rounds"));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return summary;
    }
    
}
