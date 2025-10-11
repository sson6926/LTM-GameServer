/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.game_server.dao;

import com.game_server.models.Match;
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
    
}
