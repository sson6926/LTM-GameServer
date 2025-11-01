/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.game_server.handlers;

import com.game_server.controllers.ServerThread;
import org.json.JSONObject;

/**
 *
 * @author ADMIN
 */
public class LogoutHandler implements ActionHandler{

    @Override
    public void handle(JSONObject request, ServerThread thread) {
        thread.setLoginUser(null);
    }
    
}
