package com.washu.chat.model;

import org.eclipse.jetty.websocket.api.Session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is for storing user and chat room information.
 */
public class DataStorage {
    public static Map<Session, User> userNameMap;
    public static Map<String, Session> userNameToSession;
    public static Map<String, ChatRoom> chatRoomMap;

    /**
     * Init the data source.
     */
    public static void initDataSource() {
        userNameMap = new ConcurrentHashMap<>();
        userNameToSession = new ConcurrentHashMap<>();
        chatRoomMap = new ConcurrentHashMap<>();
    }
}