package com.game_server.controllers;


import com.game_server.handlers.ActionHandler;
import com.game_server.handlers.GetOnlineUsersHandler;
import com.game_server.handlers.InviteHandler;
import com.game_server.handlers.InviteResponseHandler;
import com.game_server.handlers.LoginHandler;
import com.game_server.models.User;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerThread implements Runnable {
    private User loginUser;
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
        actionHandlers.put("LOGIN", new LoginHandler());
        // Có thể thêm các action khác ở đây, ví dụ:
        // actionHandlers.put("REGISTER", new RegisterHandler());
        actionHandlers.put("GET_ONLINE_USERS", new GetOnlineUsersHandler());
        actionHandlers.put("INVITE", new InviteHandler());
        actionHandlers.put("INVITE_RESPONSE", new InviteResponseHandler());
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String message;
            while (!isClosed) {
                message = in.readLine();
                if (message == null) break;
                handleMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void handleMessage(String message) {
        try {
            JSONObject receivedJson = new JSONObject(message);
            String action = receivedJson.optString("action", "");
            ActionHandler handler = actionHandlers.get(action);
            if (handler != null) {
                handler.handle(receivedJson, this);
            } else {
                sendMessage(new JSONObject()
                        .put("type", action)
                        .put("status", "fail")
                        .put("message", "Unknown action: " + action)
                );
            }
        } catch (Exception e) {
            sendMessage(new JSONObject()
                    .put("type", "error")
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
                serverThreadBus.broadCastToAll();
            }
            
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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