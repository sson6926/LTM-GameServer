package com.game_server.dao;

import com.game_server.utils.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;

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
}