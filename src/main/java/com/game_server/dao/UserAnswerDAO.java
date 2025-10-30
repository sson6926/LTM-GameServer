package com.game_server.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.game_server.models.UserAnswer;

public class UserAnswerDAO extends DAO {
     public UserAnswerDAO() {
        super();
    }
    public int save(UserAnswer userAnswer) {
        String sql = "INSERT INTO UserAnswer (time_completed, status, RoundDetailid) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userAnswer.getTimeCompleted());
            ps.setString(2, userAnswer.getStatus());
            ps.setInt(3, userAnswer.getRoundDetailId());
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
    public List<UserAnswer> getAnswersForRound(int matchId, int roundNumber) {
        RoundDAO roundDAO = new RoundDAO();
        List<UserAnswer> answers = new ArrayList<>();

        // Bước 1: Lấy roundId tương ứng với matchId và roundNumber
        int roundId = roundDAO.getRoundIdByMatchAndNumber(matchId, roundNumber);
        if (roundId == -1) {
            System.out.println("⚠️ Không tìm thấy roundId cho matchId=" + matchId + ", roundNumber=" + roundNumber);
            return answers;
        }

        // Bước 2: Lấy danh sách câu trả lời của tất cả người chơi trong round đó
        String sql = """
            SELECT ua.id AS ua_id,
                ua.time_completed,
                ua.status,
                ua.RoundDetailid,
                rd.Userid AS user_id,
                rd.Roundid AS round_id
            FROM UserAnswer ua
            JOIN RoundDetail rd ON ua.RoundDetailid = rd.id
            WHERE rd.Roundid = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roundId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserAnswer answer = new UserAnswer();
                    answer.setId(rs.getInt("ua_id"));
                    answer.setTimeCompleted(rs.getInt("time_completed"));
                    answer.setStatus(rs.getString("status"));
                    answer.setRoundDetailId(rs.getInt("RoundDetailid"));

                    // Nếu model UserAnswer có các field phụ như userId hoặc roundId
                    // thì bạn có thể set thêm:
                    // answer.setUserId(rs.getInt("user_id"));
                    // answer.setRoundId(rs.getInt("round_id"));

                    answers.add(answer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return answers;
    }


}
