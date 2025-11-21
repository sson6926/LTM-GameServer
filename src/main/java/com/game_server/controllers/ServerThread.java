package com.game_server.controllers;


import com.game_server.handlers.ActionHandler;
import com.game_server.handlers.GetOnlineUsersHandler;
import com.game_server.handlers.GetRankingHandler;
import com.game_server.handlers.InviteContinueNextGameHandler;
import com.game_server.handlers.InviteContinueNextGameResponseHandler;
import com.game_server.handlers.InviteUserToGameHandler;
import com.game_server.handlers.InviteUserToGameResponseHandler;
import com.game_server.handlers.LoginHandler;
import com.game_server.handlers.LogoutHandler;
import com.game_server.handlers.RegisterHandler;
import com.game_server.handlers.SubmitAnswerHandler;
import com.game_server.handlers.QuitGameHandler;
import com.game_server.models.User;

// thêm join phòng
import com.game_server.handlers.CreateRoomHandler;
import com.game_server.handlers.JoinRoomHandler;
import com.game_server.handlers.LeaveRoomHandler;

import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerThread implements Runnable {
    public User loginUser;
    private int clientId;
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private boolean isClosed;
    private final Map<String, ActionHandler> actionHandlers = new HashMap<>();
    private ServerThreadBus serverThreadBus;

    public ServerThread(Socket socket, int clientId, ServerThreadBus serverThreadBus) {
        this.clientId = clientId;
        this.socket = socket;
        this.serverThreadBus = serverThreadBus;
        this.isClosed = false;
        registerHandlers();
    }

    private void registerHandlers() {
        actionHandlers.put("LOGIN_REQUEST", new LoginHandler());
        actionHandlers.put("REGISTER_REQUEST", new RegisterHandler());
        actionHandlers.put("GET_ONLINE_USERS", new GetOnlineUsersHandler());
        actionHandlers.put("INVITE_USER_TO_GAME", new InviteUserToGameHandler());
        actionHandlers.put("INVITE_USER_TO_GAME_RESPONSE", new InviteUserToGameResponseHandler());
        actionHandlers.put("SUBMIT_USER_ANSWER", new SubmitAnswerHandler());
        actionHandlers.put("INVITE_USER_TO_NEXT_GAME", new InviteContinueNextGameHandler());
        actionHandlers.put("INVITE_USER_TO_NEXT_GAME_RESPONSE", new InviteContinueNextGameResponseHandler());
        actionHandlers.put("LOGOUT_REQUEST", new LogoutHandler());
        actionHandlers.put("QUIT_GAME", new QuitGameHandler());
        actionHandlers.put("GET_RANKING_REQUEST", new GetRankingHandler());

        // thêm join phòng
        actionHandlers.put("CREATE_ROOM_REQUEST", new CreateRoomHandler());
        actionHandlers.put("JOIN_ROOM_REQUEST", new JoinRoomHandler());
        actionHandlers.put("LEAVE_ROOM_REQUEST", new LeaveRoomHandler());




        
        // Có thể thêm các action khác ở đây, ví dụ:
        // actionHandlers.put("REGISTER", new RegisterHandler());
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String message;
            while (!isClosed) {
                message = in.readLine();
                if (message == null) {
                    System.out.println("Client " + clientId + " disconnected (message == null)");
                    break;
                }
                handleMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            handleClientDisconnect();
            closeConnection();
        }
    }

    private void handleMessage(String message) {
        try {
            JSONObject receivedJson = new JSONObject(message);
            System.err.println("Received from client " + clientId + ": " + receivedJson.toString());
            String action = receivedJson.optString("action", "");
            ActionHandler handler = actionHandlers.get(action);
            if (handler != null) {
                handler.handle(receivedJson, this);
            } else {
                sendMessage(new JSONObject()
                        .put("action", action)
                        .put("status", "fail")
                        .put("message", "Unknown action: " + action)
                );
            }
        } catch (Exception e) {
            sendMessage(new JSONObject()
                    .put("action", "error")
                    .put("status", "fail")
                    .put("message", "Invalid request format")
            );
        }
    }

    public void sendMessage(JSONObject json) {
        try {
            out.write(json.toString());
            out.newLine();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        isClosed = true;
        try {
            
            if (serverThreadBus != null) {
                serverThreadBus.remove(this);
            }
            
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Socket closed for client " + clientId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void handleClientDisconnect() {
        System.out.println("Handling disconnect for client " + clientId);
        
        if (this.loginUser != null) {
            // Đánh dấu offline
            this.loginUser.setOnline(false);
            this.loginUser.setPlaying(false);
            System.out.println("User " + this.loginUser.getUsername() + " marked as offline");
        }
        
        // Xóa khỏi danh sách
        this.serverThreadBus.remove(this);
        System.out.println("Removed client " + clientId + " from thread bus");
        
        // Thông báo cho các client khác
        this.serverThreadBus.broadCastToAll();
        System.out.println("Broadcasted updated online users list");
    }

    public ServerThreadBus getServerThreadBus() {
        return this.serverThreadBus;
    }
    public void setLoginUser(User user) {
        this.loginUser = user;
    }
    
    public User getLoginUser() {
        return this.loginUser;
    }
}