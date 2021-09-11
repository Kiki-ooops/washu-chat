package com.washu.chat.model.payload.response;

import com.google.gson.Gson;
import com.washu.chat.model.ChatRoom;
import com.washu.chat.model.User;
import com.washu.chat.model.message.GetListWrapper;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.List;

import static com.washu.chat.model.DataStorage.*;

/**
 * get a json representation for responses the data
 */
public class GetListResponseAdaptor implements ResponseAdapter {

    private String username;

    /**
     * Constructor.
     * @param username the username
     */
    public GetListResponseAdaptor(String username) {
        this.username = username;
    }

    /**
     * Get the serialized json represented data.
     * @param gson an Gson instance.
     * @return the serialized data object
     */
    public String getJsonRepresentation(Gson gson) {
        List<ChatRoom> groupOwnerList = new ArrayList<>();
        List<ChatRoom> groupMemberList = new ArrayList<>();
        List<ChatRoom> otherList = new ArrayList<>();

        Session userSess = userNameToSession.get(username);
        User user = userNameMap.get(userSess);

        for (ChatRoom chatRoom : chatRoomMap.values()) {
            if (chatRoom.isJoined(user)) {
                if (chatRoom.getOwner().equals(username)) {
                    groupOwnerList.add(chatRoom);
                } else {
                    groupMemberList.add(chatRoom);
                }
            } else {
                otherList.add(chatRoom);
            }
        }
        GetListWrapper glw = new GetListWrapper(groupOwnerList, groupMemberList, otherList);
        return gson.toJson(glw);
    }
}
