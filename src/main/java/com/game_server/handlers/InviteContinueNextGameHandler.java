package com.game_server.handlers;

import org.json.JSONObject;

import com.game_server.controllers.ServerThread;

public class InviteContinueNextGameHandler  implements ActionHandler{

    @Override
    public void handle(JSONObject request, ServerThread thread) {
      
            JSONObject responseJson = new JSONObject();
            responseJson.put("action", "INVITE_NEXT_GAME_REQUEST");
            responseJson.put("inviterUsername", thread.getLoginUser().getUsername());
            responseJson.put("inviterNickname", thread.getLoginUser().getNickname());

            for (ServerThread t : thread.getServerThreadBus().getListServerThreads()) {
                if (t.getLoginUser().getUsername().equals(request.optString("targetUsername"))) {
                    t.sendMessage(responseJson);
                    break;
                }
            }
        
    }
}