package edu.rice.comp504.model.command;

import edu.rice.comp504.model.ChatRoom;
import edu.rice.comp504.model.User;
import edu.rice.comp504.model.payload.response.CreateGroupResponseAdaptor;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

import static edu.rice.comp504.model.constant.Constant.gson;
import static edu.rice.comp504.model.DataStorage.chatRoomMap;
import static edu.rice.comp504.model.DataStorage.userNameMap;

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
