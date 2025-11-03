package com.game_server.handlers;

import com.game_server.controllers.ServerThread;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ADMIN
 */
public class GetOnlineUsersHandler implements ActionHandler {

    @Override
    public void handle(JSONObject request, ServerThread thread) {  
        JSONArray usersArray = new JSONArray();
        for (ServerThread t : thread.getServerThreadBus().getListServerThreads()) {
            if (t.getLoginUser() != null  && t.getLoginUser().isOnline())  {
                JSONObject userJson = new JSONObject();
                userJson.put("id", t.getLoginUser().getId());
                userJson.put("username", t.getLoginUser().getUsername());
                userJson.put("nickname", t.getLoginUser().getNickname());
                userJson.put("isOnline", t.getLoginUser().isOnline());
                userJson.put("isPlaying", t.getLoginUser().isPlaying());
                userJson.put("totalScore", t.getLoginUser().getTotalScore());
                userJson.put("totalWins", t.getLoginUser().getTotalWins());
                userJson.put("totalMatches", t.getLoginUser().getTotalMatches());


                usersArray.put(userJson);
            }
        }
        
        // Create response JSON
        JSONObject responseJson = new JSONObject();
        responseJson.put("action", "GET_ONLINE_USERS_RESPONSE");
        responseJson.put("onlineUsers", usersArray);
        
        //Send to client
        for (ServerThread t : thread.getServerThreadBus().getListServerThreads()) {
            if (t.getLoginUser() != null) {
                t.sendMessage(responseJson); //chi gui den thread co user da login
            }
        }
        

    }

}
