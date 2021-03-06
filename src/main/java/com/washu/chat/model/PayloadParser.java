package com.washu.chat.model;

import com.washu.chat.model.constant.Constant;
import com.washu.chat.model.message.Message;
import com.washu.chat.model.message.RequestPayload;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;

/**
 * A utility class for parsing the payload.
 */
public class PayloadParser {

    /**
     * Create the user.
     * @param userSess the user session
     * @param payload the payload
     * @return the user
     */
    public static User createUser(Session userSess, String payload) {
        User user = Constant.gson.fromJson(payload, User.class);
        user.setSession(userSess);
        user.setJoinedGroup(new ArrayList<>());
        return user;
    }

    /**
     * Create the chatRoom.
     * @param payload the payload
     * @return the chatRoom
     */
    public static ChatRoom createChatRoom(String payload) {
        ChatRoom chatRoom = Constant.gson.fromJson(payload, ChatRoom.class);
        return chatRoom;
    }

    /**
     * Create the message.
     * @param payload the payload
     * @return the message
     */
    public static Message createMessage(String payload) {
        Message message = Constant.gson.fromJson(payload, Message.class);
        return message;
    }

    /**
     * Create the request payload.
     * @param payload the payload
     * @return the request payload
     */
    public static RequestPayload createRequestPayload(String payload) {
        RequestPayload requestPayload = Constant.gson.fromJson(payload, RequestPayload.class);
        return requestPayload;
    }
}
