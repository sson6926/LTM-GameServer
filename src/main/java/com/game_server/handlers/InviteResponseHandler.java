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
public class InviteResponseHandler implements ActionHandler {

    @Override
    public void handle(JSONObject request, ServerThread thread) {
        if (request.optBoolean("accepted")) {
            JSONObject responseJson = new JSONObject();
            responseJson.put("type", "START_GAME");

            thread.sendMessage(responseJson);
            for (ServerThread t : thread.getServerThreadBus().getListServerThreads()) {
                if (t.getLoginUser().getUsername().equals(request.optString("inviterUsername"))) {
                    t.sendMessage(responseJson);
                    break;
                }
            }

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

}
