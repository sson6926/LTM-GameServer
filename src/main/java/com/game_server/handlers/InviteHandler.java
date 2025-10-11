/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.game_server.handlers;

import com.game_server.controllers.ServerThread;
import org.json.JSONObject;

/**
 *
 * @author ADMIN
 */
public class InviteHandler implements ActionHandler {

    @Override
    public void handle(JSONObject request, ServerThread thread) {
        JSONObject responseJson = new JSONObject();
        responseJson.put("type", "INVITE");
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
