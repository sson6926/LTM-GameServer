/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.game_server.handlers;

import com.game_server.controllers.ServerThread;
import com.game_server.dao.MatchDAO;
import com.game_server.dao.MatchUserDAO;
import com.game_server.dao.UserDAO;
import com.game_server.models.Match;
import com.game_server.models.MatchUser;
import com.game_server.models.User;
import com.game_server.services.QuestionGenerator;
import java.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ADMIN
 */
public class InviteResponseHandler implements ActionHandler {

    @Override
    public void handle(JSONObject request, ServerThread thread) {
        if (request.optBoolean("accepted")) {
            
            UserDAO userDAO = new UserDAO();
            User user1 = userDAO.getUserByUsername(thread.getLoginUser().getUsername());
            User user2 = userDAO.getUserByUsername(request.optString("inviterUsername"));
            
            Match match = new Match(LocalDateTime.now(), "", false);
            MatchDAO matchDAO = new MatchDAO();
            match.setId(matchDAO.save(match));
            
            
            QuestionGenerator.QuestionData questionData = QuestionGenerator.generateQuestion();
            
            JSONObject questionJson = createQuestionJson(questionData);
            
            
            JSONObject responseJson = new JSONObject();
            responseJson.put("type", "START_GAME");
            responseJson.put("matchId", match.getId());
            responseJson.put("question", questionJson);
            
            
            thread.sendMessage(responseJson);
            thread.getLoginUser().setPlaying(true);
            for (ServerThread t : thread.getServerThreadBus().getListServerThreads()) {
                if (t.getLoginUser().getUsername().equals(request.optString("inviterUsername"))) {
                    t.sendMessage(responseJson);
                    t.getLoginUser().setPlaying(true);
                    break;
                }
            }
            
            thread.getServerThreadBus().broadCastToAll();
            
        } else {
            JSONObject responseJson = new JSONObject();
            responseJson.put("type", "INVITE_RESULT");
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
