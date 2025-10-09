package com.game_server.handlers;
import com.game_server.controllers.ServerThread;
import org.json.JSONObject;

public class RegisterHandler implements ActionHandler {
    @Override
    public void handle(JSONObject request, ServerThread thread) {
        String username = request.optString("username", "");
        String password = request.optString("password", "");
        // Giả sử bạn có một UserService để xử lý đăng ký người dùng
        // UserService userService = new UserService();
        JSONObject responseJson = new JSONObject();
        responseJson.put("type", "REGISTER");
        // if (userService.register(username, password)) {
        //     responseJson.put("status", "success");
        //     responseJson.put("message", "Registration successful");
        // } else {
        //     responseJson.put("status", "fail");
        //     responseJson.put("message", "Username already exists");
        // }
        // Tạm thời giả lập thành công
        responseJson.put("status", "success");
        responseJson.put("message", "Registration successful (simulated)");
        thread.sendMessage(responseJson);
    }
}
