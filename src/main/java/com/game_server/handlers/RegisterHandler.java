package com.game_server.handlers;
import com.game_server.controllers.ServerThread;
import com.game_server.dao.UserDAO;
import com.game_server.models.User;
import org.json.JSONObject;

public class RegisterHandler implements ActionHandler {
    @Override
    public void handle(JSONObject request, ServerThread thread) {
        JSONObject responseJson = new JSONObject();
        responseJson.put("action", "REGISTER_RESPONSE");
        String username = request.optString("username", "");
        String password = request.optString("password", "");
        String nickname = request.optString("nickname", "Player");
        UserDAO userDAO = new UserDAO();
        if(userDAO.checkUsername(username)) {
            responseJson.put("status", "failed");
            responseJson.put("message", "Username has been taken");
        } else {
            User newUser = new User(username, password, nickname);
            boolean isAdded = userDAO.addUser(newUser);
            if(isAdded) {
                responseJson.put("status", "success");
                responseJson.put("message", "Registration successful");
            } else {
                responseJson.put("status", "failed");
                responseJson.put("message", "Registration failed");
            }
        }
        thread.sendMessage(responseJson);
    }
}
