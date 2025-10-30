package com.game_server.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RoundDAO extends DAO{
    public RoundDAO() {
        super();
    }
    public int save(int matchId) {
        String sql = "INSERT INTO Round (MatchId, created_at) VALUES (?, NOW())";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, matchId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public int getRoundIdByMatchAndNumber (int matchId, int roundNumber) {
        String sql = "SELECT id FROM Round WHERE Matchid = ? ORDER BY created_at ASC LIMIT 1 OFFSET ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, matchId);
            ps.setInt(2, roundNumber - 1); // roundNumber is 1-based
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
       return -1;
    }
    public boolean haveBothPlayersAnswered(int matchId, int roundNumber) {
        int roundId = getRoundIdByMatchAndNumber(matchId, roundNumber);
        if (roundId == -1) return false;

        String sql = """
            SELECT COUNT(DISTINCT rd.Userid) AS player_count
            FROM UserAnswer ua
            JOIN RoundDetail rd ON ua.RoundDetailid = rd.id
            WHERE rd.Roundid = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roundId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("player_count") == 2;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    
}
