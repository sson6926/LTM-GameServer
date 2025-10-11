package com.game_server.services;

import com.game_server.dao.UserDAO;
import com.game_server.models.User;
import java.util.List;

public class UserService {
    private UserDAO userDAO;

    public UserService() {
        userDAO = new UserDAO();
    }

    public boolean login(String username, String password) {
        User user = userDAO.verifyUser(new User(username, password));
        if (user != null) {
            userDAO.updateToOnline(user.getId());
            return true;
        }
        return false;
    }

    public boolean register(User user) {
        if (userDAO.checkDuplicateUsername(user.getUsername())) {
            return false;
        }
        return userDAO.addUser(user);
    }
    
    public List<User> getOnlineUsers() {
        return userDAO.getOnlineUsers();
    }
    
}