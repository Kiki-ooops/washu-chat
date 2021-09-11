package com.washu.chat.model.command;

import com.washu.chat.model.payload.response.CreateGroupResponseAdaptor;
import com.washu.chat.model.ChatRoom;
import com.washu.chat.model.User;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

import static com.washu.chat.model.constant.Constant.gson;
import static com.washu.chat.model.DataStorage.chatRoomMap;
import static com.washu.chat.model.DataStorage.userNameMap;

/**
 * Create chat room command.
 */
public class CreateGroupCmd extends AbstractCmd {
    private Session owner;
    private ChatRoom chatRoom;

    /**
     * Constructor.
     * @param owner the owner of the chatRoom
     * @param chatRoom the created chatRoom
     */
    public CreateGroupCmd(Session owner, ChatRoom chatRoom) {
        this.owner = owner;
        this.chatRoom = chatRoom;
    }

    /**
     * Main logic of the command.
     * @param user the receiver of the command.
     */
    public void execute(User user) {
        chatRoom.addUser(user);
        chatRoomMap.put(chatRoom.getGroupName(), chatRoom);
        userNameMap.get(owner).getJoinedGroup().add(chatRoom.getGroupName());

        //create notified message
        String msg =
                new CreateGroupResponseAdaptor(
                        user.getUsername(),
                        chatRoom.getGroupName()
                ).getJsonRepresentation(gson);

        //notify all users
        for (Session userSess : userNameMap.keySet()) {
            try {
                userSess.getRemote().sendString(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
