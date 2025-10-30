package com.game_server.controllers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class ServerThreadBus {
    private final List<ServerThread> listServerThreads;

    public ServerThreadBus() {
        listServerThreads = new ArrayList<>();
    }

    public List<ServerThread> getListServerThreads() {
        return listServerThreads;
    }

    public void add(ServerThread serverThread) {
        listServerThreads.add(serverThread);
    }
    public void remove(ServerThread serverThread) {
        listServerThreads.remove(serverThread);
    }

    public int getLength() {
        return listServerThreads.size();
    }
     public void broadCastToAll() {
        JSONArray usersArray = new JSONArray();
        for (ServerThread t : this.listServerThreads) {
            if (t.getLoginUser() != null && t.getLoginUser().isOnline()) {
                JSONObject userJson = new JSONObject();
                userJson.put("id", t.getLoginUser().getId());
                userJson.put("username", t.getLoginUser().getUsername());
                userJson.put("nickname", t.getLoginUser().getNickname());
                userJson.put("isOnline", t.getLoginUser().isOnline());
                userJson.put("isPlaying", t.getLoginUser().isPlaying());

                usersArray.put(userJson);
            }
        }
        
        // Create response JSON
        JSONObject responseJson = new JSONObject();
        responseJson.put("action", "GET_ONLINE_USERS_RESPONSE");
        responseJson.put("onlineUsers", usersArray);
        
        //Send to client
        for (ServerThread t : this.listServerThreads) {
            t.sendMessage(responseJson);
        }
    }
}
