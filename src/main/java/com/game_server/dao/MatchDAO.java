package com.game_server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import com.game_server.models.Match;
import com.game_server.models.MatchSummary;
import com.game_server.models.User;

public class MatchDAO extends DAO {
     public MatchDAO() {
        super();
    }
    
    public int save(Match match) {
        String sql = "INSERT INTO `Match` (start_time, end_time, Userid1, Userid2) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setTimestamp(1, Timestamp.valueOf(match.getStartTime()));
            ps.setTimestamp(2, match.getEndTime() != null ? Timestamp.valueOf(match.getEndTime()) : null);
            ps.setInt(3, match.getPlayer1().getId());
            ps.setInt(4, match.getPlayer2().getId());

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
        String sql = "SELECT id, start_time, end_time, result, status, Userid1, Userid2 FROM `Match` WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, matchId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserDAO userDAO = new UserDAO(); 
                    User user1 = userDAO.getUserById(rs.getInt("Userid1"));
                    User user2 = userDAO.getUserById(rs.getInt("Userid2"));

                    return new Match(
                        rs.getInt("id"),
                        rs.getTimestamp("start_time").toLocalDateTime(),
                        rs.getTimestamp("end_time") != null ? rs.getTimestamp("end_time").toLocalDateTime() : null,
                        rs.getString("result"),
                        rs.getString("status"),
                        user1,
                        user2
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    public boolean updateResult(int matchId, String result) {
        String sql = """
            UPDATE `Match`
            SET end_time = ?, 
                result = ?, 
                status = 'FINISHED'
            WHERE id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(2, result);
            ps.setInt(3, matchId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public MatchSummary getMatchSummary(int matchId, int user1Id, int user2Id) {
        MatchSummary summary = new MatchSummary();
        summary.setMatchId(matchId);

        // ✅ Lấy tổng điểm và số trận thắng cho từng user trong trận
        String sql = """
            SELECT rd.Userid, SUM(rd.score) AS total_score, SUM(rd.is_winner) AS total_wins
            FROM RoundDetail rd
            JOIN Round r ON rd.Roundid = r.id
            WHERE r.matchId = ?
            GROUP BY rd.Userid
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, matchId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("Userid");
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

            // ✅ Đếm tổng số vòng trong trận
            String roundSql = "SELECT COUNT(*) AS total_rounds FROM Round WHERE MatchId = ?";
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
