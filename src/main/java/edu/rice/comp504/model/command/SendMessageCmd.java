package edu.rice.comp504.model.command;

import edu.rice.comp504.model.ChatRoom;
import edu.rice.comp504.model.User;
import edu.rice.comp504.model.message.Message;

import static edu.rice.comp504.model.DataStorage.*;

/**
 * Send message command.
 */
public class SendMessageCmd extends AbstractCmd {
    private Message message;

    /**
     * Constructor.
     * @param message the input message
     */
    public SendMessageCmd(Message message) {
        this.message = message;
    }

    /**
     * Main logic of the command.
     * @param user the receiver of the command.
     */
    public void execute(User user) {
        ChatRoom chatRoom = chatRoomMap.get(message.getGroupName());
        chatRoom.notifyUser(message, user);
    }
}
