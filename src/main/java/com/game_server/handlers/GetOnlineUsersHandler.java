/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.game_server.handlers;

import com.game_server.controllers.ServerThread;
import com.game_server.models.User;
import com.game_server.services.UserService;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ADMIN
 */
public class GetOnlineUsersHandler implements ActionHandler {

    @Override
    public void handle(JSONObject request, ServerThread thread) {
        UserService userService = new UserService();

        List<User> onlineUsers = userService.getOnlineUsers();
        JSONArray usersArray = new JSONArray();
        for (User user : onlineUsers) {
            
            JSONObject userJson = new JSONObject();
            userJson.put("id", user.getId());
            userJson.put("username", user.getUsername());
            userJson.put("nickname", user.getNickname());
            userJson.put("isOnline", user.isOnline());
            userJson.put("isPlaying", user.isPlaying());

            usersArray.put(userJson);
        }
        
        // Create response JSON
        JSONObject responseJson = new JSONObject();
        responseJson.put("type", "GET_ONLINE_USERS");
        responseJson.put("onlineUsers", usersArray);
        
        //Send to client
        for (ServerThread t : thread.getServerThreadBus().getListServerThreads()) {
            t.sendMessage(responseJson);
        }
        

    }

}
