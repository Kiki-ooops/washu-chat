package com.washu.chat.model.command;

import com.washu.chat.model.message.Message;
import com.washu.chat.model.ChatRoom;
import com.washu.chat.model.User;

import static com.washu.chat.model.DataStorage.*;

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
