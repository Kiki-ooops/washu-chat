package edu.rice.comp504.model.command;

import edu.rice.comp504.model.ChatRoom;
import edu.rice.comp504.model.User;
import edu.rice.comp504.model.payload.response.JoinGroupResponseAdapter;

import static edu.rice.comp504.model.constant.Constant.gson;

/**
 * Join the group command.
 */
public class JoinGroupCmd extends AbstractCmd {

    private ChatRoom chatRoom;

    public JoinGroupCmd(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    /**
     * Main logic of the command.
     * @param user the receiver of the command.
     */
    public void execute(User user) {
        if (qualifiedToJoin(user, chatRoom)) {
            user.joinGroup(chatRoom.getGroupName());
            chatRoom.addUser(user);
            String message =
                    new JoinGroupResponseAdapter(user.getUsername(), chatRoom.getGroupName())
                            .getJsonRepresentation(gson);
            chatRoom.sendSystemNotification(message);
        }
    }

    /**
     * Check whether the user is qualified to join the room.
     * @param user the unchecked user
     * @param chatRoom the target chatRoom
     * @return the status whether the user is able to join in
     */
    private boolean qualifiedToJoin(User user, ChatRoom chatRoom) {
        return user.getAge() >= chatRoom.getMinAge()
                && user.getAge() <= chatRoom.getMaxAge()
                && chatRoom.getLocations().contains(user.getLocation())
                && chatRoom.getSchools().contains(user.getSchool());
    }
}
