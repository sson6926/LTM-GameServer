package com.game_server.dao;

import com.game_server.models.Match;
import com.game_server.models.User;
import org.json.JSONObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DAO {
    public UserDAO() {
        super();
    }

    public User verifyUser(User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        String sql = "SELECT * FROM User WHERE username = ? AND password = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("nickname"),
                            rs.getInt("total_matches"),
                            rs.getInt("total_wins"),
                            rs.getInt("total_score")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addUser(User user) {
        String sql = "INSERT INTO User (username, password, nickname) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getNickname());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkUsername(String username) {
        String sql = "SELECT id FROM User WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public User getUserById(int id) {
        String sql = "SELECT * FROM User WHERE id = ?";
        User user = null;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("nickname"),
                        rs.getInt("total_matches"),
                        rs.getInt("total_wins"),
                        rs.getInt("total_score")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM User WHERE username = ?";
        User user = null;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("nickname"),
                        rs.getInt("total_matches"),
                        rs.getInt("total_wins"),
                        rs.getInt("total_score")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
    public boolean updateUserStats(int userId, int matchIncrement, int winIncrement, int scoreIncrement) {
        String sql = """
            UPDATE User
            SET total_matches = total_matches + ?,
                total_wins = total_wins + ?,
                total_score = GREATEST(total_score + ?, 0)
            WHERE id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, matchIncrement);
            ps.setInt(2, winIncrement);
            ps.setInt(3, scoreIncrement);
            ps.setInt(4, userId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<User> getAll(){
        ArrayList<User> res = new ArrayList<>();
        
        String sql = "SELECT * FROM User;";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("nickname"),
                        rs.getInt("total_matches"),
                        rs.getInt("total_wins"),
                        rs.getInt("total_score")
                    );
                   res.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return res;
    }
    
    public static void main(String[] args) {
        // for testing purposes
    }

}