package com.game_server.handlers;

import com.game_server.controllers.ServerThread;
import com.game_server.dao.UserDAO;
import com.game_server.models.User;
import org.json.JSONObject;

public class LoginHandler implements ActionHandler {
    @Override
    public void handle(JSONObject request, ServerThread thread) {
        System.out.println(request.toString());
        String username = request.optString("username", "");
        String password = request.optString("password", "");
        JSONObject responseJson = new JSONObject();
        responseJson.put("action", "LOGIN_RESPONSE");
        UserDAO userDAO = new UserDAO();
        User user = userDAO.verifyUser(new User(username, password));
        if (user != null) {
            responseJson.put("status", "success");
            responseJson.put("message", "Login successful");
            responseJson.put("user", user.toJson());
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