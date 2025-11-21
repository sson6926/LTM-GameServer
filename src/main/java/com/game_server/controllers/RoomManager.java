package com.game_server.controllers;

import com.game_server.models.Room;
import com.game_server.models.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoomManager {
    private static RoomManager instance;
    private final Map<String, Room> rooms; // roomCode -> Room

    private RoomManager() {
        rooms = new ConcurrentHashMap<>();
    }

    public static RoomManager getInstance() {
        if (instance == null) {
            synchronized (RoomManager.class) {
                if (instance == null) {
                    instance = new RoomManager();
                }
            }
        }
        return instance;
    }

    public Room createRoom(User host) {
        Room room = new Room(host);
        rooms.put(room.getRoomCode(), room);
        System.out.println("Room created with code: " + room.getRoomCode());
        return room;
    }

    public Room getRoomByCode(String roomCode) {
        return rooms.get(roomCode);
    }

    public void removeRoom(String roomCode) {
        rooms.remove(roomCode);
        System.out.println("Room removed: " + roomCode);
    }

    public Room getRoomByUser(User user) {
        for (Room room : rooms.values()) {
            for (User player : room.getPlayers()) {
                if (player.getUsername().equals(user.getUsername())) {
                    return room;
                }
            }
        }
        return null;
    }

    public Map<String, Room> getAllRooms() {
        return rooms;
    }
}