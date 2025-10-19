package com.game_server.handlers;
import com.game_server.controllers.ServerThread;
import org.json.JSONObject;

public class RegisterHandler implements ActionHandler {
    @Override
    public void handle(JSONObject request, ServerThread thread) {
        String username = request.optString("username", "");
        String password = request.optString("password", "");
        JSONObject responseJson = new JSONObject();
        responseJson.put("action", "REGISTER_RESPONSE");
        responseJson.put("status", "success");
        responseJson.put("message", "Registration successful (simulated)");
        thread.sendMessage(responseJson);
    }
}
