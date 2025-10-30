package com.game_server.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.game_server.models.RoundDetail;

public class RoundDetailDAO extends DAO {
     public RoundDetailDAO() {
        super();
    }
    public int save(RoundDetail roundDetail) {
        String sql = "INSERT INTO RoundDetail (Roundid, Userid) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, roundDetail.getRound_id());
            ps.setInt(2, roundDetail.getUser_id());
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
    public boolean updateResult(int roundDetailId, int score, int is_winner) {
        String sql = "UPDATE RoundDetail SET score = ?, is_winner = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, score);
            ps.setInt(2, is_winner);
            ps.setInt(3, roundDetailId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public List<RoundDetail> getRoundDetailsByMatchAndRound(int matchId, int roundNumber) {
        RoundDAO roundDAO = new RoundDAO();
        List<RoundDetail> roundDetails = new ArrayList<>();

        // 1) Lấy roundId bằng hàm đã có
        int roundId = roundDAO.getRoundIdByMatchAndNumber(matchId, roundNumber);
        if (roundId == -1) return roundDetails; // không tìm thấy

        // 2) Lấy tất cả RoundDetail của roundId
        String sql = "SELECT id, score, is_winner, Roundid, Userid FROM RoundDetail WHERE Roundid = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roundId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RoundDetail rd = new RoundDetail();
                    rd.setId(rs.getInt("id"));
                    rd.setScore(rs.getInt("score"));
                    rd.setIs_winner(rs.getInt("is_winner"));
                    rd.setRound_id(rs.getInt("Roundid"));
                    rd.setUser_id(rs.getInt("Userid"));
                    roundDetails.add(rd);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return roundDetails;
    }


}
