/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.game_server.handlers;

import com.game_server.controllers.ServerThread;
import com.game_server.dao.UserDAO;
import com.game_server.models.User;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ADMIN
 */
public class GetRankingHandler implements ActionHandler{

    @Override
    public void handle(JSONObject request, ServerThread thread) {
        UserDAO ud = new UserDAO();
        List<User> users = ud.getAll();
        users.sort(new Comparator<>(){
            @Override
            public int compare(User o1, User o2) {
                return o2.getTotalScore() - o1.getTotalScore();
            }
        });
        
        JSONArray usersArray = new JSONArray();
        for (User u : users) {
            JSONObject userJson = new JSONObject();
            userJson.put("id", u.getId());
            userJson.put("username", u.getUsername());
            userJson.put("nickname", u.getNickname());
            userJson.put("totalMatches", u.getTotalMatches());
            userJson.put("totalWins", u.getTotalWins());
            userJson.put("totalScore", u.getTotalScore());
            
            usersArray.put(userJson);
        }
        
        JSONObject responseJson = new JSONObject();
        responseJson.put("action", "GET_RANKING_RESPONSE");
        responseJson.put("ranking", usersArray);
        
        thread.sendMessage(responseJson);
        
    }
    
}
