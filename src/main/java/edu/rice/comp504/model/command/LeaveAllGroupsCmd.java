package edu.rice.comp504.model.command;

import edu.rice.comp504.model.ChatRoom;
import edu.rice.comp504.model.User;

import static edu.rice.comp504.model.constant.Constant.LEAVE_REASON_FORCE_TO_LEAVE;
import static edu.rice.comp504.model.DataStorage.*;

/**
 * Leave all groups command.
 */
public class LeaveAllGroupsCmd extends LeaveGroupCmd {

    /**
     * Constructor.
     */
    public LeaveAllGroupsCmd() {
        super(null);
    }

    /**
     * Main logic of the command.
     * @param user the receiver of the command.
     */
    public void execute(User user) {
        for (String chatRoomId : user.getJoinedGroup()) {
            ChatRoom chatRoom = chatRoomMap.get(chatRoomId);
            if (chatRoom.isJoined(user)) {
                chatRoom.removeUser(user);
                boolean isDissolved = isChatRoomEmpty(chatRoom, user);
                if (isDissolved) {
                    clearEmptyRoom(chatRoom);
                    removeEmptyRoomFromUser(chatRoom);
                }
                String message = buildReason(isDissolved, chatRoom, user, LEAVE_REASON_FORCE_TO_LEAVE);
                chatRoom.sendSystemNotification(message);
            }
        }
        user.leaveAllGroup();
    }
}
