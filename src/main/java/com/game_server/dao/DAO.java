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

//    public static void main(String[] args) {
//        //test connect and  query 1+1
//        DAO dao = new DAO();
//        try {
//            var stmt = dao.connection.createStatement();
//            var rs = stmt.executeQuery("SELECT 1+1");
//            if (rs.next()) {
//                System.out.println("1+1=" + rs.getInt(1));
//            }
//            } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}