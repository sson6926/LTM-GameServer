package com.game_server.services;

import com.game_server.dao.UserDAO;
import com.game_server.models.User;

public class UserService {
    private UserDAO userDAO;

    public UserService() {
        userDAO = new UserDAO();
    }

    public boolean login(String username, String password) {
        return userDAO.verifyUser(new User(username, password)) != null;
    }

    public boolean register(User user) {
        if (userDAO.checkDuplicateUsername(user.getUsername())) {
            return false;
        }
        return userDAO.addUser(user);
    }
}