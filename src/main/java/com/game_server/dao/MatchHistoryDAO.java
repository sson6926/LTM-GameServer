package com.game_server.dao;

import com.game_server.models.MatchHistory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatchHistoryDAO extends DAO {  //

    /**
     * Lấy lịch sử trận đấu của user
     * @param userId ID của user
     * @param limit Số lượng trận gần nhất (ví dụ: 10)
     * @return List các MatchHistory
     */
    public List<MatchHistory> getMatchHistoryByUserId(int userId, int limit) {
        List<MatchHistory> historyList = new ArrayList<>();

        // ✅ QUERY ĐÃ FIX - Loại bỏ check status
        String sql = """
        SELECT 
            m.id as matchId,
            CASE 
                WHEN m.Userid1 = ? THEN u2.username 
                ELSE u1.username 
            END as opponentUsername,
            CASE 
                WHEN m.Userid1 = ? THEN u2.nickname 
                ELSE u1.nickname 
            END as opponentNickname,
            CASE 
                WHEN m.Userid1 = ? THEN COALESCE(SUM(CASE WHEN rd.Userid = m.Userid1 THEN rd.score ELSE 0 END), 0)
                ELSE COALESCE(SUM(CASE WHEN rd.Userid = m.Userid2 THEN rd.score ELSE 0 END), 0)
            END as userTotalScore,
            CASE 
                WHEN m.Userid1 = ? THEN COALESCE(SUM(CASE WHEN rd.Userid = m.Userid2 THEN rd.score ELSE 0 END), 0)
                ELSE COALESCE(SUM(CASE WHEN rd.Userid = m.Userid1 THEN rd.score ELSE 0 END), 0)
            END as opponentTotalScore,
            CASE 
                WHEN m.Userid1 = ? AND COALESCE(SUM(CASE WHEN rd.Userid = m.Userid1 THEN rd.score ELSE 0 END), 0) > 
                                       COALESCE(SUM(CASE WHEN rd.Userid = m.Userid2 THEN rd.score ELSE 0 END), 0) THEN 'WIN'
                WHEN m.Userid1 = ? AND COALESCE(SUM(CASE WHEN rd.Userid = m.Userid1 THEN rd.score ELSE 0 END), 0) < 
                                       COALESCE(SUM(CASE WHEN rd.Userid = m.Userid2 THEN rd.score ELSE 0 END), 0) THEN 'LOSS'
                WHEN m.Userid2 = ? AND COALESCE(SUM(CASE WHEN rd.Userid = m.Userid2 THEN rd.score ELSE 0 END), 0) > 
                                       COALESCE(SUM(CASE WHEN rd.Userid = m.Userid1 THEN rd.score ELSE 0 END), 0) THEN 'WIN'
                WHEN m.Userid2 = ? AND COALESCE(SUM(CASE WHEN rd.Userid = m.Userid2 THEN rd.score ELSE 0 END), 0) < 
                                       COALESCE(SUM(CASE WHEN rd.Userid = m.Userid1 THEN rd.score ELSE 0 END), 0) THEN 'LOSS'
                ELSE 'DRAW'
            END as result,
            m.start_time,
            m.end_time,
            COUNT(DISTINCT r.id) as roundCount
        FROM `Match` m
        JOIN User u1 ON m.Userid1 = u1.id
        JOIN User u2 ON m.Userid2 = u2.id
        LEFT JOIN Round r ON m.id = r.Matchid
        LEFT JOIN RoundDetail rd ON r.id = rd.Roundid
        WHERE (m.Userid1 = ? OR m.Userid2 = ?)
              AND EXISTS (
                  SELECT 1 FROM Round r2 
                  JOIN RoundDetail rd2 ON r2.id = rd2.Roundid 
                  WHERE r2.Matchid = m.id
              )
        GROUP BY m.id
        ORDER BY m.start_time DESC
        LIMIT ?
        """;

        try (Connection conn = DAO.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set parameters (11 tham số)
            pstmt.setInt(1, userId);   // WHEN opponentUsername
            pstmt.setInt(2, userId);   // WHEN opponentNickname
            pstmt.setInt(3, userId);   // WHEN userTotalScore
            pstmt.setInt(4, userId);   // WHEN opponentTotalScore
            pstmt.setInt(5, userId);   // WHEN result - user1 win
            pstmt.setInt(6, userId);   // WHEN result - user1 loss
            pstmt.setInt(7, userId);   // WHEN result - user2 win
            pstmt.setInt(8, userId);   // WHEN result - user2 loss
            pstmt.setInt(9, userId);   // WHERE Userid1
            pstmt.setInt(10, userId);  // WHERE Userid2
            pstmt.setInt(11, limit);   // LIMIT

            System.out.println("🔍 Executing query for userId: " + userId + ", limit: " + limit);
            ResultSet rs = pstmt.executeQuery();

            int count = 0;
            while (rs.next()) {
                MatchHistory match = new MatchHistory(
                        rs.getInt("matchId"),
                        rs.getString("opponentUsername"),
                        rs.getString("opponentNickname"),
                        rs.getString("result"),
                        rs.getInt("userTotalScore"),
                        rs.getInt("opponentTotalScore"),
                        rs.getTimestamp("start_time") != null ?
                                rs.getTimestamp("start_time").toLocalDateTime() : null,
                        rs.getTimestamp("end_time") != null ?
                                rs.getTimestamp("end_time").toLocalDateTime() : null,
                        rs.getInt("roundCount")
                );
                historyList.add(match);
                count++;
                System.out.println("✅ Match " + count + ": " + match);
            }

            System.out.println("✅ Total matches found: " + count);
        } catch (SQLException e) {
            System.err.println("❌ Error fetching match history: " + e.getMessage());
            e.printStackTrace();
        }

        return historyList;
    }


}