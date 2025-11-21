package com.game_server.handlers;

import com.game_server.controllers.RoomManager;
import com.game_server.controllers.ServerThread;
import com.game_server.models.Room;
import com.game_server.models.User;
import org.json.JSONObject;

public class CreateRoomHandler implements ActionHandler {

    @Override
    public void handle(JSONObject request, ServerThread thread) {
        User currentUser = thread.getLoginUser();

        if (currentUser == null) {
            JSONObject response = new JSONObject();
            response.put("action", "CREATE_ROOM_RESPONSE");
            response.put("status", "fail");
            response.put("message", "User not logged in");
            thread.sendMessage(response);
            return;
        }

        // Kiểm tra xem user đã ở trong phòng nào chưa
        RoomManager roomManager = RoomManager.getInstance();
        Room existingRoom = roomManager.getRoomByUser(currentUser);

        if (existingRoom != null) {
            JSONObject response = new JSONObject();
            response.put("action", "CREATE_ROOM_RESPONSE");
            response.put("status", "fail");
            response.put("message", "You are already in a room");
            thread.sendMessage(response);
            return;
        }

        // Tạo phòng mới
        Room newRoom = roomManager.createRoom(currentUser);

        JSONObject response = new JSONObject();
        response.put("action", "CREATE_ROOM_RESPONSE");
        response.put("status", "success");
        response.put("message", "Room created successfully");
        response.put("room", newRoom.toJson());
        thread.sendMessage(response);

        System.out.println("User " + currentUser.getUsername() + " created room " + newRoom.getRoomCode());
    }
}