package com.game_server.handlers;

import java.util.List;

import org.json.JSONObject;

import com.game_server.controllers.Server;
import com.game_server.controllers.ServerThread;
import com.game_server.dao.MatchDAO;
import com.game_server.dao.RoundDAO;
import com.game_server.dao.RoundDetailDAO;
import com.game_server.dao.UserAnswerDAO;
import com.game_server.dao.UserDAO;
import com.game_server.models.Match;
import com.game_server.models.MatchSummary;
import com.game_server.models.Round;
import com.game_server.models.RoundDetail;
import com.game_server.models.UserAnswer;

public class SubmitAnswerHandler implements ActionHandler {

    private final MatchDAO matchDAO = new MatchDAO();
    private final RoundDetailDAO roundDetailDAO = new RoundDetailDAO();
    private final RoundDAO roundDAO = new RoundDAO();
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
            int roundId = roundDAO.getRoundIdByMatchAndNumber(matchId, roundNumber);
            RoundDetail roundDetail = new RoundDetail(roundId, userId);
            UserAnswer answer = new UserAnswer(timeCompleted, status, roundDetailDAO.save(roundDetail));
            int saved = userAnswerDAO.save(answer);
            System.out.println(" Received USER_ANSWER from user " + userId + " -> " + status);
            boolean bothAnswered = roundDAO.haveBothPlayersAnswered(matchId, roundNumber);

            // Khi cả 2 người chơi đã có kết quả
           if (bothAnswered) {
                System.out.println("Both players have submitted answers for round " + roundNumber);
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

    private void determineWinner(int matchId, int roundNumber) {
        List<UserAnswer> answers = userAnswerDAO.getAnswersForRound(matchId, roundNumber);
        List<RoundDetail> roundDetails = roundDetailDAO.getRoundDetailsByMatchAndRound(matchId, roundNumber);
        System.err.println("Amount of answers fetched: " + answers.size());
        if (answers.size() < 2) {
            System.out.println(" Not enough answers for match " + matchId + " round " + roundNumber);
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
                score1 = 3; score2 = 2;
            } else if (a2.getTimeCompleted() < a1.getTimeCompleted()) {
                result1 = "LOSE"; result2 = "WIN";
                score1 = 2; score2 = 3;
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
            result1 = result2 = "LOSE";
            score1 = score2 = -3; 
            System.out.println("Both players wrong - draw");
        }
        // Cả hai TIMEOUT → cả hai thua
        // Một TIMEOUT, một CORRECT → CORRECT thắng
        // Một TIMEOUT, một WRONG → cả hai thua
        // Cả hai CORRECT → so thời gian
        // Một CORRECT, một WRONG → CORRECT thắng
        // Cả hai WRONG → hòa

        RoundDetail rd1 = roundDetails.get(0);
        RoundDetail rd2 = roundDetails.get(1);
        roundDetailDAO.updateResult(rd1.getId(), score1, result1.equals("WIN") ? 1 : 0);
        roundDetailDAO.updateResult(rd2.getId(), score2, result2.equals("WIN") ? 1 : 0);

       

        System.out.println(" Match " + matchId + " ended. Results: User "
            + rd1.getUser_id() + " -> " + result1 + " (" + score1 + " points), User "
            + rd2.getUser_id() + " -> " + result2 + " (" + score2 + " points)");

        if (a1.getStatus().equals("QUIT") || a2.getStatus().equals("QUIT")) {
            System.out.println("One of the players quit. Ending match " + matchId);
            JSONObject finalResultMsg = createGameFinalResultMessage(matchId, rd1, rd2, result1, result2, score1, score2,a1,a2);
            // Gửi kết quả cuối cùng cho cả hai user
            int user1Id = rd1.getUser_id();
            int user2Id = rd2.getUser_id();

            for (ServerThread t : Server.getServerThreadBus().getListServerThreads()) {
                if (t.getLoginUser() == null) continue;

                int currentUserId = t.getLoginUser().getId();
                if (currentUserId == user1Id || currentUserId == user2Id) {
                    t.getLoginUser().setPlaying(false);
                    t.sendMessage(finalResultMsg);
                    System.out.println("Sent final result to user " + currentUserId);
                }
            }
            System.out.println("Game finished due to player quitting. Final results sent to both players.");
        } else {

            JSONObject resultMsg = createGameResultMessage(matchId, rd1, rd2, result1, result2, score1, score2,a1,a2);

            // Gửi kết quả cho cả hai user
            int user1Id = rd1.getUser_id();
            int user2Id = rd2.getUser_id();

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
    }

    private JSONObject createGameResultMessage(int matchId, RoundDetail rd1, RoundDetail rd2, 
                                         String result1, String result2, int score1, int score2,
                                         UserAnswer a1, UserAnswer a2) {
        JSONObject resultMsg = new JSONObject();
        resultMsg.put("action", "GAME_RESULT");
        resultMsg.put("matchId", matchId);
        resultMsg.put("roundScore1", score1);
        resultMsg.put("roundScore2", score2);
        resultMsg.put("roundResult1", result1);
        resultMsg.put("roundResult2", result2);

        MatchSummary summary = matchDAO.getMatchSummary(matchId, rd1.getUser_id(), rd2.getUser_id());

        JSONObject player1Result = new JSONObject()
            .put("userId", rd1.getUser_id())
            .put("result", result1)
            .put("score", score1)
            .put("time", a1.getTimeCompleted())
            .put("totalScore", summary.getUser1TotalScore())  // Tổng điểm từ match_user
            .put("totalWins", summary.getUser1Wins());        // Tổng win từ match_user

        JSONObject player2Result = new JSONObject()
            .put("userId", rd2.getUser_id())
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
     private JSONObject createGameFinalResultMessage(int matchId, RoundDetail rd1, RoundDetail rd2, 
                                         String result1, String result2, int score1, int score2,
                                         UserAnswer a1, UserAnswer a2) {
        
        JSONObject resultMsg = new JSONObject();
        resultMsg.put("action", "GAME_FINAL_RESULT");
        resultMsg.put("matchId", matchId);
        MatchSummary summary = matchDAO.getMatchSummary(matchId, rd1.getUser_id(), rd2.getUser_id());
        UserDAO userDAO = new UserDAO();
        if( summary.getUser1Wins()  > summary.getUser2Wins()){
            MatchDAO matchDAO = new MatchDAO();
            matchDAO.updateResult( matchId, rd1.getUser_id()+"");
            userDAO.updateUserStats(rd1.getUser_id(), 1, 1, summary.getUser1TotalScore());
            userDAO.updateUserStats(rd2.getUser_id(), 1, 0, summary.getUser2TotalScore());

        }
        else if( summary.getUser1Wins()  < summary.getUser2Wins()){
            MatchDAO matchDAO = new MatchDAO();
            matchDAO.updateResult( matchId, rd2.getUser_id()+"");
            userDAO.updateUserStats(rd2.getUser_id(), 1, 1, summary.getUser2TotalScore());
            userDAO.updateUserStats(rd1.getUser_id(), 1, 0, summary.getUser1TotalScore());
        }
        else{
            if (summary.getUser1TotalScore()  > summary.getUser2TotalScore()){
                MatchDAO matchDAO = new MatchDAO();
                matchDAO.updateResult( matchId, rd1.getUser_id()+"");
                userDAO.updateUserStats(rd1.getUser_id(), 1, 1, summary.getUser1TotalScore());
                userDAO.updateUserStats(rd2.getUser_id(), 1, 0, summary.getUser2TotalScore());
            }
            else if( summary.getUser1TotalScore()  < summary.getUser2TotalScore()){
                MatchDAO matchDAO = new MatchDAO();
                matchDAO.updateResult( matchId, rd2.getUser_id()+"");
                userDAO.updateUserStats(rd2.getUser_id(), 1, 1, summary.getUser2TotalScore());
                userDAO.updateUserStats(rd1.getUser_id(), 1, 0, summary.getUser1TotalScore());
            }
            MatchDAO matchDAO = new MatchDAO();
            matchDAO.updateResult( matchId, "DRAW");
        }
        JSONObject player1Result = new JSONObject()
            .put("userId", rd1.getUser_id())
            .put("result", result1)
            .put("score", score1)
            .put("time", a1.getTimeCompleted())
            .put("totalScore", summary.getUser1TotalScore())  // Tổng điểm từ match_user
            .put("totalWins", summary.getUser1Wins());        // Tổng win từ match_user

        JSONObject player2Result = new JSONObject()
            .put("userId", rd2.getUser_id())
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

