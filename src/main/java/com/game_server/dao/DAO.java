package com.game_server.dao;

import com.game_server.utils.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAO {
    protected Connection connection;


    public DAO() {
        try {
            Class.forName(ConfigLoader.getDriver());
            connection = DriverManager.getConnection(
                    ConfigLoader.getUrl(),
                    ConfigLoader.getUsername(),
                    ConfigLoader.getPassword()
            );
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Connection to database failed");
        }

    }
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(ConfigLoader.getDriver());
            return DriverManager.getConnection(
                    ConfigLoader.getUrl(),
                    ConfigLoader.getUsername(),
                    ConfigLoader.getPassword()
            );
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found", e);
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}