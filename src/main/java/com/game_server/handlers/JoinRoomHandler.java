package com.game_server.handlers;

import com.game_server.controllers.RoomManager;
import com.game_server.controllers.Server;
import com.game_server.controllers.ServerThread;
import com.game_server.models.Room;
import com.game_server.models.User;
import org.json.JSONArray;
import org.json.JSONObject;
import com.game_server.models.Match;

public class JoinRoomHandler implements ActionHandler {

    @Override

//    public void handle(JSONObject request, ServerThread thread) {
//        User currentUser = thread.getLoginUser();
//        String roomCode = request.optString("roomCode", "");
//
//        if (currentUser == null) {
//            sendResponse(thread, "fail", "User not logged in", null);
//            return;
//        }
//
//        if (roomCode.isEmpty()) {
//            sendResponse(thread, "fail", "Room code is required", null);
//            return;
//        }
//
//        RoomManager roomManager = RoomManager.getInstance();
//
//        // Kiểm tra xem user đã ở trong phòng nào chưa
//        Room existingRoom = roomManager.getRoomByUser(currentUser);
//        if (existingRoom != null) {
//            sendResponse(thread, "fail", "You are already in a room", null);
//            return;
//        }
//
//        // Tìm phòng theo code
//        Room room = roomManager.getRoomByCode(roomCode);
//
//        if (room == null) {
//            sendResponse(thread, "fail", "Room not found", null);
//            return;
//        }
//
//        if (room.getStatus().equals("PLAYING")) {
//            sendResponse(thread, "fail", "Room is already in game", null);
//            return;
//        }
//
//        if (!room.addPlayer(currentUser)) {
//            sendResponse(thread, "fail", "Room is full", null);
//            return;
//        }
//
//        // Thông báo cho người join thành công
//        sendResponse(thread, "success", "Joined room successfully", room);
//
//        // Thông báo cho tất cả người trong phòng về người chơi mới
//        notifyRoomPlayers(room);
//
//        System.out.println("User " + currentUser.getUsername() + " joined room " + roomCode);
//    }
    public void handle(JSONObject request, ServerThread thread) {
        User currentUser = thread.getLoginUser();
        String roomCode = request.optString("roomCode", "");

        if (currentUser == null) {
            sendResponse(thread, "fail", "User not logged in", null);
            return;
        }

        if (roomCode.isEmpty()) {
            sendResponse(thread, "fail", "Room code is required", null);
            return;
        }

        RoomManager roomManager = RoomManager.getInstance();

        // Kiểm tra xem user đã ở trong phòng nào chưa
        Room existingRoom = roomManager.getRoomByUser(currentUser);
        if (existingRoom != null) {
            sendResponse(thread, "fail", "You are already in a room", null);
            return;
        }

        // Tìm phòng theo code
        Room room = roomManager.getRoomByCode(roomCode);

        if (room == null) {
            sendResponse(thread, "fail", "Room not found", null);
            return;
        }

        if (room.getStatus().equals("PLAYING")) {
            sendResponse(thread, "fail", "Room is already in game", null);
            return;
        }

        if (!room.addPlayer(currentUser)) {
            sendResponse(thread, "fail", "Room is full", null);
            return;
        }

        // Thông báo cho người join thành công
        sendResponse(thread, "success", "Joined room successfully", room);

        // Thông báo cho tất cả người trong phòng về người chơi mới
        notifyRoomPlayers(room);

        System.out.println("User " + currentUser.getUsername() + " joined room " + roomCode);

        // 🔥 NẾU ĐỦ 2 NGƯỜI → TỰ ĐỘNG START GAME
        if (room.getPlayers().size() == room.getMaxPlayers()) {
            System.out.println("Room " + roomCode + " is READY! Starting game...");
            room.setStatus("PLAYING");
            startGame(room);
        }
    }

    private void sendResponse(ServerThread thread, String status, String message, Room room) {
        JSONObject response = new JSONObject();
        response.put("action", "JOIN_ROOM_RESPONSE");
        response.put("status", status);
        response.put("message", message);
        if (room != null) {
            response.put("room", room.toJson());
        }
        thread.sendMessage(response);
    }

    //
//    private void notifyRoomPlayers(Room room) {
//        JSONObject notification = new JSONObject();
//        notification.put("action", "ROOM_UPDATED");
//        notification.put("room", room.toJson());
//
//        JSONArray playersArray = new JSONArray();
//        for (User player : room.getPlayers()) {
//            playersArray.put(player.toJson());
//        }
//        notification.put("players", playersArray);
//
//        // Gửi thông báo cho tất cả người chơi trong phòng
//        for (User player : room.getPlayers()) {
//            ServerThread playerThread = findThreadByUsername(player.getUsername());
//            if (playerThread != null) {
//                playerThread.sendMessage(notification);
//            }
//        }
//    }
    // sửa để gửi cả chủ phongf
    private void notifyRoomPlayers(Room room) {
        JSONObject notification = new JSONObject();
        notification.put("action", "ROOM_UPDATED");
        notification.put("room", room.toJson());

        JSONArray playersArray = new JSONArray();
        for (User player : room.getPlayers()) {
            playersArray.put(player.toJson());
        }
        notification.put("players", playersArray);

        // 🔥 DEBUG: In ra để kiểm tra
        System.out.println("=== NOTIFYING ROOM PLAYERS ===");
        System.out.println("Room: " + room.getRoomCode());
        System.out.println("Players count: " + room.getPlayers().size());

        // Gửi thông báo cho TẤT CẢ người chơi trong phòng
        for (User player : room.getPlayers()) {
            System.out.println("Sending to: " + player.getUsername());
            ServerThread playerThread = findThreadByUsername(player.getUsername());
            if (playerThread != null) {
                playerThread.sendMessage(notification);
                System.out.println("✅ Sent to " + player.getUsername());
            } else {
                System.out.println("❌ Thread not found for " + player.getUsername());
            }
        }
        System.out.println("=== END NOTIFICATION ===");
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

    // start game
    private void startGame(Room room) {
        try {
            // Lấy 2 người chơi
            User user1 = room.getPlayers().get(0);
            User user2 = room.getPlayers().get(1);

            // Tạo match mới (tương tự InviteUserToGameResponseHandler)
            Match match = new Match(java.time.LocalDateTime.now(), null, user1, user2);
            com.game_server.dao.MatchDAO matchDAO = new com.game_server.dao.MatchDAO();
            match.setId(matchDAO.save(match));

            com.game_server.dao.RoundDAO roundDAO = new com.game_server.dao.RoundDAO();
            roundDAO.save(match.getId());

            // Tạo câu hỏi
            com.game_server.services.QuestionGenerator.QuestionData questionData =
                    com.game_server.services.QuestionGenerator.generateQuestion();

            JSONObject questionJson = createQuestionJson(questionData);

            // Tạo thông tin user
            JSONObject user1Json = new JSONObject();
            user1Json.put("id", user1.getId());
            user1Json.put("username", user1.getUsername());
            user1Json.put("nickname", user1.getNickname());
            user1Json.put("totalScore", user1.getTotalScore());
            user1Json.put("totalWins", user1.getTotalWins());
            user1Json.put("totalMatches", user1.getTotalMatches());

            JSONObject user2Json = new JSONObject();
            user2Json.put("id", user2.getId());
            user2Json.put("username", user2.getUsername());
            user2Json.put("nickname", user2.getNickname());
            user2Json.put("totalScore", user2.getTotalScore());
            user2Json.put("totalWins", user2.getTotalWins());
            user2Json.put("totalMatches", user2.getTotalMatches());

            // Gửi START_GAME cho user1
            JSONObject responseToUser1 = new JSONObject();
            responseToUser1.put("action", "START_GAME");
            responseToUser1.put("matchId", match.getId());
            responseToUser1.put("question", questionJson);
            responseToUser1.put("self", user1Json);
            responseToUser1.put("opponent", user2Json);

            // Gửi START_GAME cho user2
            JSONObject responseToUser2 = new JSONObject(responseToUser1.toString());
            responseToUser2.put("self", user2Json);
            responseToUser2.put("opponent", user1Json);

            // Tìm thread và gửi
            ServerThread thread1 = findThreadByUsername(user1.getUsername());
            ServerThread thread2 = findThreadByUsername(user2.getUsername());

            if (thread1 != null) {
                thread1.sendMessage(responseToUser1);
                thread1.getLoginUser().setPlaying(true);
                System.out.println("✅ Sent START_GAME to " + user1.getUsername());
            }

            if (thread2 != null) {
                thread2.sendMessage(responseToUser2);
                thread2.getLoginUser().setPlaying(true);
                System.out.println("✅ Sent START_GAME to " + user2.getUsername());
            }

            // Broadcast online users
            Server.getServerThreadBus().broadCastToAll();

            // Xóa room sau khi start game
            RoomManager.getInstance().removeRoom(room.getRoomCode());

        } catch (Exception e) {
            System.err.println("Error starting game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JSONObject createQuestionJson(com.game_server.services.QuestionGenerator.QuestionData questionData) {
        JSONObject questionJson = new JSONObject();

        questionJson.put("typeQues", questionData.isNumbers() ? "NUMBERS" : "LETTERS");
        questionJson.put("sortOrder", questionData.isAscending() ? "ASCENDING" : "DESCENDING");
        questionJson.put("timeLimit", questionData.getTimeLimit());

        org.json.JSONArray itemsArray = new org.json.JSONArray();
        for (String item : questionData.getItems()) {
            itemsArray.put(item);
        }
        questionJson.put("items", itemsArray);

        org.json.JSONArray itemsArrayAnswer = new org.json.JSONArray();
        for (String item : questionData.getCorrectAnswer()) {
            itemsArrayAnswer.put(item);
        }
        questionJson.put("correctAnswer", itemsArrayAnswer);

        String typeText = questionData.isNumbers() ? "numbers" : "letters";
        String orderText = questionData.isAscending() ? "ascending" : "descending";
        String instruction = String.format("Sort these %s in %s order (%d items - %d seconds)",
                typeText, orderText,
                questionData.getItems().size(), questionData.getTimeLimit());
        questionJson.put("instruction", instruction);

        return questionJson;
    }
}