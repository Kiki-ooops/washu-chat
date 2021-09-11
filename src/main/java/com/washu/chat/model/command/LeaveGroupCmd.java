package com.washu.chat.model.command;

import com.washu.chat.model.payload.response.DissolveGroupResponseAdaptor;
import com.washu.chat.model.ChatRoom;
import com.washu.chat.model.User;
import com.washu.chat.model.payload.response.LeaveGroupResponseAdaptor;

import static com.washu.chat.model.DataStorage.*;
import static com.washu.chat.model.constant.Constant.*;

/**
 * Leave the group command.
 */
public class LeaveGroupCmd extends AbstractCmd {
    private ChatRoom chatRoom;

    /**
     * Constructor.
     * @param chatRoom the target chatRoom
     */
    public LeaveGroupCmd(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    /**
     * Check whether the chatRoom is empty.
     * @param chatRoom the target chatRoom
     * @param leaver the person to leave
     * @return the status whether the chatRoom is empty
     */
    public boolean isChatRoomEmpty(ChatRoom chatRoom, User leaver) {
        return chatRoom.isEmpty() || chatRoom.isOwner(leaver);
    }

    /**
     * Remove the chatRoom from the chatRoom map.
     * @param chatRoom the target chatRoom
     */
    public void clearEmptyRoom(ChatRoom chatRoom) {
        chatRoomMap.remove(chatRoom.getGroupName());
    }

    /**
     * remove the empty chatRoom from the user joined list.
     * @param chatRoom the empty chatRoom
     */
    public void removeEmptyRoomFromUser(ChatRoom chatRoom) {
        for (User joinedUser : chatRoom.getJoinedUsers()) {
            joinedUser.getJoinedGroup().remove(chatRoom.getGroupName());
        }
    }

    /**
     * Build the reason of leaving.
     * @param isDissolved the status of whether the chatRoom is dissolved.
     * @param chatRoom the target chatRoom
     * @param user the receiver
     * @param leaveReason the leaving reason
     * @return the reason
     */
    public String buildReason(boolean isDissolved, ChatRoom chatRoom, User user, String leaveReason) {
        String message = null;
        if (isDissolved) {
            message = new DissolveGroupResponseAdaptor(chatRoom.getGroupName()).getJsonRepresentation(gson);
        } else {
            message = new LeaveGroupResponseAdaptor(user.getUsername(), chatRoom.getGroupName(), leaveReason)
                    .getJsonRepresentation(gson);
        }
        return message;
    }

    /**
     * Main logic of the command.
     * @param user the receiver of the command.
     */
    public void execute(User user) {
        user.getJoinedGroup().remove(chatRoom.getGroupName());
        chatRoom.removeUser(user);
        boolean isDissolved = isChatRoomEmpty(chatRoom, user);
        if(isDissolved) {
            clearEmptyRoom(chatRoom);
            removeEmptyRoomFromUser(chatRoom);
        }
        String message = buildReason(isDissolved, chatRoom, user, LEAVE_REASON_REGULAR_LEAVE);
        chatRoom.sendSystemNotification(message);
    }
}
