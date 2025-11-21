package com.game_server.handlers;

import com.game_server.controllers.RoomManager;
import com.game_server.controllers.Server;
import com.game_server.controllers.ServerThread;
import com.game_server.models.Room;
import com.game_server.models.User;
import org.json.JSONArray;
import org.json.JSONObject;

public class LeaveRoomHandler implements ActionHandler {

    @Override
    public void handle(JSONObject request, ServerThread thread) {
        User currentUser = thread.getLoginUser();

        if (currentUser == null) {
            sendResponse(thread, "fail", "User not logged in");
            return;
        }

        RoomManager roomManager = RoomManager.getInstance();
        Room room = roomManager.getRoomByUser(currentUser);

        if (room == null) {
            sendResponse(thread, "fail", "You are not in any room");
            return;
        }

        String roomCode = room.getRoomCode();
        room.removePlayer(currentUser);

        // Nếu phòng trống, xóa phòng
        if (room.getPlayers().isEmpty()) {
            roomManager.removeRoom(roomCode);
            sendResponse(thread, "success", "Left room successfully");
        } else {
            // Thông báo cho những người còn lại trong phòng
            notifyRoomPlayers(room, currentUser.getNickname() + " left the room");
            sendResponse(thread, "success", "Left room successfully");
        }

        System.out.println("User " + currentUser.getUsername() + " left room " + roomCode);
    }

    private void sendResponse(ServerThread thread, String status, String message) {
        JSONObject response = new JSONObject();
        response.put("action", "LEAVE_ROOM_RESPONSE");
        response.put("status", status);
        response.put("message", message);
        thread.sendMessage(response);
    }

    private void notifyRoomPlayers(Room room, String message) {
        JSONObject notification = new JSONObject();
        notification.put("action", "ROOM_UPDATED");
        notification.put("room", room.toJson());
        notification.put("message", message);

        JSONArray playersArray = new JSONArray();
        for (User player : room.getPlayers()) {
            playersArray.put(player.toJson());
        }
        notification.put("players", playersArray);

        for (User player : room.getPlayers()) {
            ServerThread playerThread = findThreadByUsername(player.getUsername());
            if (playerThread != null) {
                playerThread.sendMessage(notification);
            }
        }
    }

    private ServerThread findThreadByUsername(String username) {
        for (ServerThread thread : Server.getServerThreadBus().getListServerThreads()) {
            if (thread.getLoginUser() != null &&
                    thread.getLoginUser().getUsername().equals(username)) {
                return thread;
            }
        }
        return null;
    }
}