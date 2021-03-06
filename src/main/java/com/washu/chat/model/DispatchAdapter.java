package com.washu.chat.model;

import com.washu.chat.model.command.*;
import com.washu.chat.model.constant.Constant;
import com.washu.chat.model.message.Message;
import com.washu.chat.model.message.RequestPayload;
import com.washu.chat.model.payload.response.GetListResponseAdaptor;
import com.washu.chat.model.command.*;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

import static com.washu.chat.model.DataStorage.*;
import static com.washu.chat.model.PayloadParser.*;

/**
 * Handle the communication logic between model and view.
 */
public class DispatchAdapter {
    private static DispatchAdapter da;

    private DispatchAdapter() { }

    /**
     * Get the singleton instance of dispatcher adaptor.
     * @return the dispatch adaptor.
     */
    public static DispatchAdapter getInstance() {
        if (da == null) {
            da = new DispatchAdapter();
        }
        return da;
    }

    /**
     * Create connectiong via websocket.
     * @param user the session of the connected user
     */
    public void createConnection(Session user) {
        userNameMap.put(user, new User(user));
    }

    /**
     * Update user profile via websocket.
     * @param userSess the user session
     * @param payload the payload information
     */
    public void updateProfile(Session userSess, String payload) {
        String result = Constant.SUCCESS;
        User user = createUser(userSess, payload);
        if (!userNameMap.containsKey(userSess)) {
            result = Constant.ERROR;
        } else {
            if (!userNameToSession.containsKey(user.getUsername())) {
                userNameToSession.put(user.getUsername(), userSess);
                userNameMap.put(userSess, user);
            } else {
                result = Constant.CONFLICT;
            }
        }
        try {
            userSess.getRemote().sendString(Constant.gson.toJson(new Message("register", "", user.getUsername(), result, "")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * create chat room so that other users can see, websocket
     * @param payload the payload information.
     * @return the status code.
     */
    public int creatChatRoom(String payload) {
        ChatRoom chatRoom = createChatRoom(payload);
        if (chatRoomMap.containsKey(chatRoom.getGroupName())) {
            return 409;
        }
        Session owner = userNameToSession.get(chatRoom.getOwner());
        new CreateGroupCmd(owner, chatRoom).execute(userNameMap.get(owner));
        return chatRoomMap.containsKey(chatRoom.getGroupName()) ? 200 : 409;
    }

    public int joinChatRoom(String payload) {
        RequestPayload rp = createRequestPayload(payload);
        Session userSess = userNameToSession.get(rp.getUsername());
        if (chatRoomMap.get(rp.getGroupName()).isJoined(userNameMap.get(userSess))) {
            return 403;
        }
        new JoinGroupCmd(chatRoomMap.get(rp.getGroupName())).execute(userNameMap.get(userSess));
        return chatRoomMap.get(rp.getGroupName()).isJoined(userNameMap.get(userSess)) ? 200 : 403;
    }

    public int leaveChatRoom(String payload) {
        RequestPayload rp = createRequestPayload(payload);
        Session userSess = userNameToSession.get(rp.getUsername());
        if (!chatRoomMap.get(rp.getGroupName()).isJoined(userNameMap.get(userSess))) {
            return 403;
        }
        new LeaveGroupCmd(chatRoomMap.get(rp.getGroupName())).execute(userNameMap.get(userSess));
        if (!chatRoomMap.containsKey(rp.getGroupName())) {
            return 200;
        }
        return !chatRoomMap.get(rp.getGroupName()).isJoined(userNameMap.get(userSess)) ? 200 : 403;
    }

    public void sendMessage(Message message) {
        Session userSess = userNameToSession.get(message.getSender());
        if (message.getText().contains(Constant.HATE)) {
            new LeaveAllGroupsCmd().execute(userNameMap.get(userSess));
        } else {
            new SendMessageCmd(message).execute(userNameMap.get(userSess));
        }
    }

    public String getList(String payload) {
        RequestPayload rq = createRequestPayload(payload);
        return new GetListResponseAdaptor(rq.getUsername()).getJsonRepresentation(Constant.gson);
    }

    public void closeConnection(Session session) {
        User user = userNameMap.get(session);
        new CloseConnectionCmd().execute(user);
    }
}
