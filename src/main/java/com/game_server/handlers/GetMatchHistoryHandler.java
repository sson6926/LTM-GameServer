package com.game_server.handlers;

import com.game_server.controllers.ServerThread;
import com.game_server.dao.MatchHistoryDAO;
import com.game_server.models.MatchHistory;
import com.game_server.models.User;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

public class GetMatchHistoryHandler implements ActionHandler {

    @Override
    public void handle(JSONObject request, ServerThread serverThread) {
        System.out.println("Processing GET_MATCH_HISTORY request");

        // Kiểm tra user đã login chưa
        User loginUser = serverThread.getLoginUser();
        if (loginUser == null) {
            serverThread.sendMessage(new JSONObject()
                    .put("action", "GET_MATCH_HISTORY_RESPONSE")
                    .put("status", "fail")
                    .put("message", "User not logged in")
            );
            System.err.println("GET_MATCH_HISTORY failed: User not logged in");
            return;
        }

        // Lấy limit từ request (mặc định 10 nếu không có)
        int limit = request.optInt("limit", 10);
        if (limit <= 0 || limit > 100) {
            limit = 10;  // Giới hạn tối đa 100
        }

        try {
            // Truy vấn database
            MatchHistoryDAO matchHistoryDAO = new MatchHistoryDAO();
            List<MatchHistory> matchHistory = matchHistoryDAO.getMatchHistoryByUserId(
                    loginUser.getId(),
                    limit
            );
            matchHistoryDAO.closeConnection();

            System.out.println("Found " + matchHistory.size() + " matches for user " + loginUser.getUsername());

            // Convert List sang JSONArray
            JSONArray historyArray = new JSONArray();
            for (MatchHistory match : matchHistory) {
                JSONObject matchObj = new JSONObject()
                        .put("matchId", match.getMatchId())
                        .put("opponentUsername", match.getOpponentUsername())
                        .put("opponentNickname", match.getOpponentNickname())
                        .put("result", match.getResult())
                        .put("userTotalScore", match.getUserTotalScore())
                        .put("opponentTotalScore", match.getOpponentTotalScore())
                        .put("startTime", match.getStartTime() != null ? match.getStartTime().toString() : null)
                        .put("endTime", match.getEndTime() != null ? match.getEndTime().toString() : null)
                        .put("roundCount", match.getRoundCount());

                historyArray.put(matchObj);
            }

            // Gửi response thành công
            serverThread.sendMessage(new JSONObject()
                    .put("action", "GET_MATCH_HISTORY_RESPONSE")
                    .put("status", "success")
                    .put("totalMatches", matchHistory.size())
                    .put("matchHistory", historyArray)
            );

            System.out.println("GET_MATCH_HISTORY response sent successfully");

        } catch (Exception e) {
            System.err.println("Error processing GET_MATCH_HISTORY: " + e.getMessage());
            e.printStackTrace();

            serverThread.sendMessage(new JSONObject()
                    .put("action", "GET_MATCH_HISTORY_RESPONSE")
                    .put("status", "fail")
                    .put("message", "Error retrieving match history: " + e.getMessage())
            );
        }
    }
}
