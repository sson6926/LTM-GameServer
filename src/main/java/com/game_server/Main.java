package com.game_server;

import com.game_server.controllers.Server;

public class Main {
    public static void main(String[] args) {
        Server server = Server.getInstance();
        server.start();
    }
}