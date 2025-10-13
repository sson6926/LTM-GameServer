package com.game_server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.game_server.models.UserAnswer;

public class UserAnswerDAO extends DAO {
    public UserAnswerDAO() {
        super();
    }
    public boolean saveUserAnswer(UserAnswer answer) {
        String sql = "INSERT INTO user_answers (match_id, user_id, round_number, time_completed, status) VALUES (?, ?, ?, ?, ?)";
        try (
            PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, answer.getMatchId());
            ps.setInt(2, answer.getUserId());
            ps.setInt(3, answer.getRoundNumber());  // Thêm round_number
            ps.setInt(4, answer.getTimeCompleted());
            ps.setString(5, answer.getStatus());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean haveBothPlayersAnswered(int matchId, int roundNumber) {
        String sql = "SELECT COUNT(DISTINCT user_id) as player_count FROM user_answers WHERE match_id = ? AND round_number = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, matchId);
            ps.setInt(2, roundNumber);  // Thêm điều kiện round_number
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("player_count") == 2;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<UserAnswer> getAnswersForRound(int matchId, int roundNumber) {
        List<UserAnswer> answers = new ArrayList<>();
        String sql = "SELECT * FROM user_answers WHERE match_id = ? AND round_number = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, matchId);
            ps.setInt(2, roundNumber);  // Lấy theo round_number
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UserAnswer answer = new UserAnswer(
                    rs.getInt("user_id"),
                    rs.getInt("match_id"),
                    rs.getInt("round_number"),  // Lấy round_number
                    rs.getInt("time_completed"),
                    rs.getString("status")
                );
                answers.add(answer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answers;
    }
    
}
