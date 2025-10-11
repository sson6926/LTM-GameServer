/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.game_server.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author ADMIN
 */
public class MatchUserDAO extends DAO{
    
    public MatchUserDAO() {
        super();
    }
    
    public boolean save(int matchId, int userId, int score, boolean isWinner) {
        String sql = "INSERT INTO match_user (match_id, user_id, score, is_winner) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, matchId);
            ps.setInt(2, userId);
            ps.setInt(3, score);
            ps.setBoolean(4, isWinner);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
