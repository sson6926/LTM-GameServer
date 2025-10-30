package com.game_server.handlers;

import java.time.LocalDateTime;

import org.json.JSONArray;
import org.json.JSONObject;

import com.game_server.controllers.ServerThread;
import com.game_server.dao.MatchDAO;
import com.game_server.dao.RoundDAO;
import com.game_server.dao.UserDAO;
import com.game_server.models.Match;
import com.game_server.models.User;
import com.game_server.services.QuestionGenerator;

public class InviteUserToGameResponseHandler implements ActionHandler {

    @Override
    public void handle(JSONObject request, ServerThread thread) {
        if (request.optBoolean("accepted")) {
            
            UserDAO userDAO = new UserDAO();
            User user1 = userDAO.getUserByUsername(thread.getLoginUser().getUsername());
            User user2 = userDAO.getUserByUsername(request.optString("inviterUsername"));
            JSONObject user1Json = new JSONObject();
            user1Json.put("id", user1.getId());
            user1Json.put("username", user1.getUsername());
            user1Json.put("nickname", user1.getNickname());
            user1Json.put("totalScore", user1.getTotalScore());
            user1Json.put("totalWins", user1.getTotalWins());
            user1Json.put("totalMatches", user1.getTotalMatches());

            JSONObject user2Json = new JSONObject();
            user2Json.put("id", user2.getId());
            user2Json.put("username", user2.getUsername());
            user2Json.put("nickname", user2.getNickname());
            user2Json.put("totalScore", user2.getTotalScore());
            user2Json.put("totalWins", user2.getTotalWins());
            user2Json.put("totalMatches", user2.getTotalMatches());

            
            Match match = new Match(LocalDateTime.now(), null, user1, user2);
            MatchDAO matchDAO = new MatchDAO();
            match.setId(matchDAO.save(match));
            RoundDAO roundDAO = new RoundDAO();
            roundDAO.save(match.getId());
            
            
            QuestionGenerator.QuestionData questionData = QuestionGenerator.generateQuestion();
            
            JSONObject questionJson = createQuestionJson(questionData);
            
            
           JSONObject responseToUser1 = new JSONObject();
            responseToUser1.put("action", "START_GAME");
            responseToUser1.put("matchId", match.getId());
            responseToUser1.put("question", questionJson);
            responseToUser1.put("self", user1Json);
            responseToUser1.put("opponent", user2Json);

            JSONObject responseToUser2 = new JSONObject(responseToUser1.toString());
            responseToUser2.put("self", user2Json);
            responseToUser2.put("opponent", user1Json);
            
            thread.sendMessage(responseToUser1);
            thread.getLoginUser().setPlaying(true);
            for (ServerThread t : thread.getServerThreadBus().getListServerThreads()) {
                if (t.getLoginUser().getUsername().equals(request.optString("inviterUsername"))) {
                    t.sendMessage(responseToUser2);
                    t.getLoginUser().setPlaying(true);
                    break;
                }
            }
            
            thread.getServerThreadBus().broadCastToAll();
            
        } else {
            JSONObject responseJson = new JSONObject();
            responseJson.put("action", "INVITE_USER_TO_GAME_RESULT");
            responseJson.put("opponentNickname", thread.getLoginUser().getNickname());
            responseJson.put("accepted", false);

            for (ServerThread t : thread.getServerThreadBus().getListServerThreads()) {
                if (t.getLoginUser().getUsername().equals(request.optString("inviterUsername"))) {
                    t.sendMessage(responseJson);
                    break;
                }
            }
        }
    }
    
    private JSONObject createQuestionJson(QuestionGenerator.QuestionData questionData) {
        JSONObject questionJson = new JSONObject();
        
        questionJson.put("typeQues", questionData.isNumbers() ? "NUMBERS" : "LETTERS");
        questionJson.put("sortOrder", questionData.isAscending() ? "ASCENDING" : "DESCENDING");
        questionJson.put("timeLimit", questionData.getTimeLimit());        
        
        JSONArray itemsArray = new JSONArray();
        for (String item : questionData.getItems()) {
            itemsArray.put(item);
        }
        questionJson.put("items", itemsArray);
        JSONArray itemsArrayAnswer = new JSONArray();
        for (String item : questionData.getCorrectAnswer()) {
            itemsArrayAnswer.put(item);
        }
        questionJson.put("correctAnswer", itemsArrayAnswer);
        
        
        // Tạo instruction
        String typeText = questionData.isNumbers() ? "numbers" : "letters";
        String orderText = questionData.isAscending() ? "ascending" : "descending";
        String instruction = String.format("Sort these %s in %s order (%d items - %d seconds)", 
                                         typeText, orderText, 
                                         questionData.getItems().size(), questionData.getTimeLimit());
        questionJson.put("instruction", instruction);
        
        return questionJson;
    }

}
