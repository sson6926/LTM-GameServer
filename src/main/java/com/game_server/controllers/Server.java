package com.game_server.controllers;


import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {
    private static volatile Server instance;
    private static ServerThreadBus serverThreadBus = null;

    private Server() {
        this.serverThreadBus = new ServerThreadBus();
    }

    public static Server getInstance() {
        if (instance == null) {
            synchronized (Server.class) {
                if (instance == null) {
                    instance = new Server();
                }
            }
        }
        return instance;
    }

    public void start() {
        int clientId = 0;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                10, 100, 10, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(8)
        );
        try (ServerSocket serverSocket = new ServerSocket(8989, 50, InetAddress.getByName("0.0.0.0"))) {
            System.out.println("Server is running on port 8989");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress().getHostAddress());
                ServerThread serverThread = new ServerThread(socket, ++clientId, serverThreadBus);
                serverThreadBus.add(serverThread);
                executor.execute(serverThread);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    public static ServerThreadBus getServerThreadBus() {
        return serverThreadBus;
    }
}