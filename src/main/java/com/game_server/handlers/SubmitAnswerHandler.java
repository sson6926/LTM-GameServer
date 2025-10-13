package com.game_server.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import org.json.JSONObject;

import com.game_server.controllers.Server;
import com.game_server.controllers.ServerThread;
import com.game_server.dao.MatchDAO;
import com.game_server.dao.MatchUserDAO;
import com.game_server.dao.UserAnswerDAO;
import com.game_server.models.Match;
import com.game_server.models.MatchSummary;
import com.game_server.models.UserAnswer;

public class SubmitAnswerHandler implements ActionHandler {

    private final MatchDAO matchDAO = new MatchDAO();
    private final MatchUserDAO matchUserDAO = new MatchUserDAO();
    private final UserAnswerDAO userAnswerDAO = new UserAnswerDAO();

    @Override
    public void handle(JSONObject request, ServerThread thread) {
        try {
            JSONObject data = request.getJSONObject("data");

            int matchId = data.getInt("matchId");
            int userId = data.getInt("userId");
            int roundNumber = data.optInt("roundNumber");
            int timeCompleted = data.optInt("timeCompleted", 0);
            String status = data.optString("status", "UNKNOWN");

            Match match = matchDAO.getById(matchId);
            if (match == null) {
                thread.sendMessage(new JSONObject()
                    .put("type", "RESPONSE_USER_ANSWER")
                    .put("status", "fail")
                    .put("message", "Match not found"));
                return;
            }

            // ✅ LƯU ANSWER VÀO DATABASE VỚI ROUND_NUMBER
            UserAnswer answer = new UserAnswer(userId, matchId, roundNumber, timeCompleted, status);
            boolean saved = userAnswerDAO.saveUserAnswer(answer);

            System.out.println(" Received USER_ANSWER from user " + userId + " -> " + status);

            // ✅ KIỂM TRA THEO ROUND_NUMBER
            boolean bothAnswered = userAnswerDAO.haveBothPlayersAnswered(matchId, roundNumber);

            // Khi cả 2 người chơi đã có kết quả
           if (bothAnswered) {
            System.out.println("🎯 Both players have submitted answers for round " + roundNumber);
            determineWinner(matchId, roundNumber);  // Truyền roundNumber
        }

        } catch (Exception e) {
            e.printStackTrace();
            thread.sendMessage(new JSONObject()
                .put("type", "RESPONSE_USER_ANSWER")
                .put("status", "fail")
                .put("message", "Error processing answer"));
        }
    }

    /**
     * Xử lý xác định thắng thua khi cả hai người đã trả lời
     */
    private void determineWinner(int matchId, int roundNumber) {
        List<UserAnswer> answers = userAnswerDAO.getAnswersForRound(matchId, roundNumber);
        if (answers.size() < 2) {
            System.out.println("❌ Not enough answers for match " + matchId + " round " + roundNumber);
            return;
        }
        UserAnswer a1 = answers.get(0);
        UserAnswer a2 = answers.get(1);
        String result1, result2;
        int score1 = 0, score2 = 0;

        // TH1: Cả hai đều thoát hoặc hết giờ → cả hai thua
        if ((a1.getStatus().equals("QUIT") || a1.getStatus().equals("TIMEOUT")) && 
            (a2.getStatus().equals("QUIT") || a2.getStatus().equals("TIMEOUT"))) {
            result1 = "LOSE"; 
            result2 = "LOSE";
            score1 = -3; 
            score2 = -3;
            System.out.println("Both players timed out or quit - both lose");
        }
        // TH2: Chỉ player1 thoát/hết giờ
        else if (a1.getStatus().equals("QUIT") || a1.getStatus().equals("TIMEOUT")) {
            // Player1 thua, Player2 thắng nếu CORRECT, thua nếu WRONG
            if (a2.getStatus().equals("CORRECT")) {
                result1 = "LOSE"; 
                result2 = "WIN";
                score1 = -3; 
                score2 = 3;
                System.out.println("Player 1 timed out/quit - Player 2 wins (CORRECT)");
            } else {
                result1 = "LOSE"; 
                result2 = "LOSE";
                score1 = -3; 
                score2 = -3;
                System.out.println("Player 1 timed out/quit - Player 2 also loses (WRONG)");
            }
        }
        // TH3: Chỉ player2 thoát/hết giờ
        else if (a2.getStatus().equals("QUIT") || a2.getStatus().equals("TIMEOUT")) {
            // Player2 thua, Player1 thắng nếu CORRECT, thua nếu WRONG
            if (a1.getStatus().equals("CORRECT")) {
                result1 = "WIN"; 
                result2 = "LOSE";
                score1 = 3; 
                score2 = -3;
                System.out.println("Player 2 timed out/quit - Player 1 wins (CORRECT)");
            } else {
                result1 = "LOSE"; 
                result2 = "LOSE";
                score1 = -3; 
                score2 = -3;
                System.out.println("Player 2 timed out/quit - Player 1 also loses (WRONG)");
            }
        }
        // TH4: Cả hai đều đúng → ai nhanh hơn thắng
        else if (a1.getStatus().equals("CORRECT") && a2.getStatus().equals("CORRECT")) {
            if (a1.getTimeCompleted() < a2.getTimeCompleted()) {
                result1 = "WIN"; result2 = "LOSE";
                score1 = 3; score2 = -3;
            } else if (a2.getTimeCompleted() < a1.getTimeCompleted()) {
                result1 = "LOSE"; result2 = "WIN";
                score1 = -3; score2 = 3;
            } else {
                result1 = result2 = "DRAW";
                score1 = score2 = 0;
            }
        }
        // TH5: Một đúng, một sai → đúng thắng
        else if (a1.getStatus().equals("CORRECT") && a2.getStatus().equals("WRONG")) {
            result1 = "WIN"; result2 = "LOSE";
            score1 = 3; score2 = -3;
        } else if (a2.getStatus().equals("CORRECT") && a1.getStatus().equals("WRONG")) {
            result1 = "LOSE"; result2 = "WIN";
            score1 = -3; score2 = 3;
        }
        // TH6: Còn lại → hòa (cả hai sai)
        else {
            result1 = result2 = "DRAW";
            score1 = score2 = 0; 
            System.out.println("Both players wrong - draw");
        }
        // ✅ Cả hai TIMEOUT → cả hai thua

        // ✅ Một TIMEOUT, một CORRECT → CORRECT thắng

        // ✅ Một TIMEOUT, một WRONG → cả hai thua

        // ✅ Cả hai CORRECT → so thời gian

        // ✅ Một CORRECT, một WRONG → CORRECT thắng

        // ✅ Cả hai WRONG → hòa

        // ... phần còn lại giữ nguyên
        matchDAO.updateEndTime(matchId, LocalDateTime.now());
        matchUserDAO.save(matchId, a1.getUserId(), score1, result1.equals("WIN"));
        matchUserDAO.save(matchId, a2.getUserId(), score2, result2.equals("WIN"));

        System.out.println(" Match " + matchId + " ended. Results: User "
            + a1.getUserId() + " -> " + result1 + " (" + score1 + " points), User "
            + a2.getUserId() + " -> " + result2 + " (" + score2 + " points)");

        // ✅ TÍNH TỔNG KẾT TỪ CÁC BẢNG HIỆN CÓ
        JSONObject resultMsg = createGameResultMessage(matchId, a1, a2, result1, result2, score1, score2);

        // Gửi kết quả cho cả hai user
        int user1Id = a1.getUserId();
        int user2Id = a2.getUserId();

        for (ServerThread t : Server.getServerThreadBus().getListServerThreads()) {
            if (t.getLoginUser() == null) continue;

            int currentUserId = t.getLoginUser().getId();
            if (currentUserId == user1Id || currentUserId == user2Id) {
                t.sendMessage(resultMsg);
                System.out.println("Sent result to user " + currentUserId);
            }
        }

        System.out.println("Game finished. Results sent to both players.");
    }

    private JSONObject createGameResultMessage(int matchId, UserAnswer a1, UserAnswer a2, 
                                         String result1, String result2, int score1, int score2) {
        JSONObject resultMsg = new JSONObject();
        resultMsg.put("type", "GAME_RESULT");
        resultMsg.put("matchId", matchId);
        resultMsg.put("roundScore1", score1);
        resultMsg.put("roundScore2", score2);
        resultMsg.put("roundResult1", result1);
        resultMsg.put("roundResult2", result2);

        // ✅ LẤY TỔNG KẾT TỪ CÁC BẢNG HIỆN CÓ
        MatchSummary summary = matchDAO.getMatchSummary(matchId, a1.getUserId(), a2.getUserId());

        JSONObject player1Result = new JSONObject()
            .put("userId", a1.getUserId())
            .put("result", result1)
            .put("score", score1)
            .put("time", a1.getTimeCompleted())
            .put("totalScore", summary.getUser1TotalScore())  // Tổng điểm từ match_user
            .put("totalWins", summary.getUser1Wins());        // Tổng win từ match_user

        JSONObject player2Result = new JSONObject()
            .put("userId", a2.getUserId())
            .put("result", result2)
            .put("score", score2)
            .put("time", a2.getTimeCompleted())
            .put("totalScore", summary.getUser2TotalScore())
            .put("totalWins", summary.getUser2Wins());

        JSONObject matchSummary = new JSONObject()
            .put("totalRounds", summary.getTotalRounds())
            .put("score", summary.getUser1Wins() + " - " + summary.getUser2Wins())
            .put("totalScore", summary.getUser1TotalScore() + " - " + summary.getUser2TotalScore());

        resultMsg.put("player1", player1Result);
        resultMsg.put("player2", player2Result);
        resultMsg.put("matchSummary", matchSummary);

        return resultMsg;
    }
}

