package com.game_server.handlers;
import com.game_server.controllers.ServerThread;
import com.game_server.models.User;
import com.game_server.services.UserService;
import org.json.JSONObject;

public class RegisterHandler implements ActionHandler {
    @Override
    public void handle(JSONObject request, ServerThread thread) {
        String username = request.optString("username", "");
        String nickname = request.optString("nickname", "");
        String password = request.optString("password", "");
        User user = new User(username, password, nickname);
        
        UserService userService = new UserService();
        JSONObject responseJson = new JSONObject();
        responseJson.put("type", "REGISTER_RESULT");
        if (userService.register(user)) {
            responseJson.put("success", true);
            responseJson.put("message", "Registration successful");
        } else {
            responseJson.put("success", false);
            responseJson.put("message", "Username already exists");
        }
         
        thread.sendMessage(responseJson);
    }
}
