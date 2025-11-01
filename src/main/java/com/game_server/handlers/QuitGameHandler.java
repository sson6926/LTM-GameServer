package com.game_server.handlers;

import org.json.JSONObject;

import com.game_server.controllers.Server;
import com.game_server.controllers.ServerThread;
import com.game_server.dao.MatchDAO;
import com.game_server.dao.UserDAO;
import com.game_server.models.MatchSummary;

public class QuitGameHandler implements ActionHandler {

    private final MatchDAO matchDAO = new MatchDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    public void handle(JSONObject request, ServerThread thread) {
        try {
            JSONObject data = request.getJSONObject("data");

            int matchId = data.getInt("matchId");
            int userId = data.getInt("userId");
            int opponentId = data.optInt("opponentId");
            MatchSummary matchSummary = matchDAO.getMatchSummary(matchId, userId, opponentId);

            if( matchSummary.getUser1Wins()  > matchSummary.getUser2Wins()){
            matchDAO.updateResult( matchId, userId+"");
            userDAO.updateUserStats(userId, 1, 1, matchSummary.getUser1TotalScore());
            userDAO.updateUserStats(opponentId, 1, 0, matchSummary.getUser2TotalScore());

            }
            else if( matchSummary.getUser1Wins()  < matchSummary.getUser2Wins()){
                MatchDAO matchDAO = new MatchDAO();
                matchDAO.updateResult( matchId, opponentId+"");
                userDAO.updateUserStats(opponentId, 1, 1, matchSummary.getUser2TotalScore());
                userDAO.updateUserStats(userId, 1, 0, matchSummary.getUser1TotalScore());
            }
            else{
                if (matchSummary.getUser1TotalScore()  > matchSummary.getUser2TotalScore()){
                    MatchDAO matchDAO = new MatchDAO();
                    matchDAO.updateResult( matchId, userId+"");
                    userDAO.updateUserStats(userId, 1, 1, matchSummary.getUser1TotalScore());
                    userDAO.updateUserStats(opponentId, 1, 0, matchSummary.getUser2TotalScore());
                }
                else if( matchSummary.getUser1TotalScore()  < matchSummary.getUser2TotalScore()){
                    MatchDAO matchDAO = new MatchDAO();
                    matchDAO.updateResult( matchId, opponentId+"");
                    userDAO.updateUserStats(opponentId, 1, 1, matchSummary.getUser2TotalScore());
                    userDAO.updateUserStats(userId, 1, 0, matchSummary.getUser1TotalScore());
                }
                MatchDAO matchDAO = new MatchDAO();
                matchDAO.updateResult( matchId, "DRAW");
                userDAO.updateUserStats(userId, 1, 0, matchSummary.getUser1TotalScore());
                userDAO.updateUserStats(opponentId, 1, 0, matchSummary.getUser2TotalScore());
            }
            JSONObject resultMsg = new JSONObject();
            resultMsg.put("action", "GAME_FINAL_RESULT");
            resultMsg.put("matchId", matchId);
            JSONObject player1Result = new JSONObject()
                .put("userId", userId)
                .put("totalScore", matchSummary.getUser1TotalScore())  // Tổng điểm từ match_user
                .put("totalWins", matchSummary.getUser1Wins());        // Tổng win từ match_user

            JSONObject player2Result = new JSONObject()
                .put("userId", opponentId)
                .put("totalScore", matchSummary.getUser2TotalScore())
                .put("totalWins", matchSummary.getUser2Wins());

            JSONObject matchSummaryfinal = new JSONObject()
                .put("totalRounds", matchSummary.getTotalRounds())
                .put("score", matchSummary.getUser1Wins() + " - " + matchSummary.getUser2Wins())
                .put("totalScore", matchSummary.getUser1TotalScore() + " - " + matchSummary.getUser2TotalScore());

            resultMsg.put("player1", player1Result);
            resultMsg.put("player2", player2Result);
            resultMsg.put("matchSummary", matchSummaryfinal);
            for (ServerThread t : Server.getServerThreadBus().getListServerThreads()) {
                if (t.getLoginUser() == null) continue;

                int currentUserId = t.getLoginUser().getId();
                if (currentUserId == userId || currentUserId == opponentId) {
                    t.getLoginUser().setPlaying(false);
                    t.sendMessage(resultMsg);
                    System.out.println("Sent final result to user " + currentUserId);
                }
            }

            }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}