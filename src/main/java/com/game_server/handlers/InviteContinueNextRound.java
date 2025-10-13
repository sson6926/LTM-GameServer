package com.game_server.handlers;

import org.json.JSONObject;

import com.game_server.controllers.ServerThread;
import com.game_server.models.Match;
import com.game_server.models.UserAnswer;

public class InviteContinueNextRound implements ActionHandler{

    @Override
    public void handle(JSONObject request, ServerThread thread) {
       
        // try {
        //  JSONObject responseJson = new JSONObject();
        // responseJson.put("type", "INVITE");
        // responseJson.put("inviterUsername", thread.getLoginUser().getUsername());
        // responseJson.put("inviterNickname", thread.getLoginUser().getNickname());
        
        // for (ServerThread t : thread.getServerThreadBus().getListServerThreads()) {
        //     if (t.getLoginUser().getUsername().equals(request.optString("targetUsername"))) {
        //         t.sendMessage(responseJson);
        //         break;
        //     }
        // }
        // }
    }
     
}
