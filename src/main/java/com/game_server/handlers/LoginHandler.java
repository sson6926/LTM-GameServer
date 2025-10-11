package com.game_server.handlers;

import com.game_server.controllers.ServerThread;
import com.game_server.models.User;
import com.game_server.services.UserService;
import org.json.JSONObject;

public class LoginHandler implements ActionHandler {
    @Override
    public void handle(JSONObject request, ServerThread thread) {
        String username = request.optString("username", "");
        String password = request.optString("password", "");
        UserService userService = new UserService();
        JSONObject responseJson = new JSONObject();
        responseJson.put("type", "LOGIN");
        
        User user = userService.login(username, password);
        if (user != null) {
            responseJson.put("status", "success");
            responseJson.put("message", "Login successful");
            responseJson.put("username", user.getUsername());
            responseJson.put("nickname", user.getNickname());
        } else {
            responseJson.put("status", "fail");
            responseJson.put("message", "Invalid username or password");
        }
        
        user.setOnline(true);
        user.setPlaying(false);
        thread.setLoginUser(user);
        thread.sendMessage(responseJson);
    }
}