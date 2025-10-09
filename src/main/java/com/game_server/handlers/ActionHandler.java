package com.game_server.handlers;

import com.game_server.controllers.ServerThread;
import org.json.JSONObject;

public interface ActionHandler {
    void handle(JSONObject request, ServerThread thread);
}