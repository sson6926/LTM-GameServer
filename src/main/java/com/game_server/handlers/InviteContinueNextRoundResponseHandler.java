package com.game_server.handlers;

import java.time.LocalDateTime;

import org.json.JSONArray;
import org.json.JSONObject;

import com.game_server.controllers.ServerThread;
import com.game_server.dao.MatchDAO;
import com.game_server.dao.UserDAO;
import com.game_server.models.Match;
import com.game_server.models.User;
import com.game_server.services.QuestionGenerator;

public class InviteContinueNextRoundResponseHandler implements ActionHandler {

    @Override
    public void handle(JSONObject request, ServerThread thread) {
        if (request.optBoolean("accepted")) {        
            
            QuestionGenerator.QuestionData questionData = QuestionGenerator.generateQuestion();
            
            JSONObject questionJson = createQuestionJson(questionData);
        
            JSONObject responseToUser = new JSONObject();
            responseToUser.put("type", "CONTINUE_NEXT_ROUND");
            responseToUser.put("question", questionJson);
 

            thread.sendMessage(responseToUser);
            thread.getLoginUser().setPlaying(true);
            for (ServerThread t : thread.getServerThreadBus().getListServerThreads()) {
                if (t.getLoginUser().getUsername().equals(request.optString("inviterUsername"))) {
                    t.sendMessage(responseToUser);
                    t.getLoginUser().setPlaying(true);
                    break;
                }
            }
            
            thread.getServerThreadBus().broadCastToAll();
            
        } else {
            JSONObject responseJson = new JSONObject();
            responseJson.put("type", "INVITE_NEXT_ROUND_RESULT");
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
