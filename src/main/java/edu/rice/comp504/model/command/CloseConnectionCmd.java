package edu.rice.comp504.model.command;

import edu.rice.comp504.model.ChatRoom;
import edu.rice.comp504.model.User;

import java.util.HashSet;
import java.util.Set;

import static edu.rice.comp504.model.DataStorage.*;
import static edu.rice.comp504.model.constant.Constant.LEAVE_REASON_CLOSE_CONNECTION;

/**
 * Close connection command.
 */
public class CloseConnectionCmd extends LeaveGroupCmd {

    /**
     * Constructor.
     */
    public CloseConnectionCmd() {
        super(null);
    }

    /**
     * Main logic of the command.
     * @param user the receiver of the command.
     */
    public void execute(User user) {
        Set<String> emptyChatroom = new HashSet<>();
        for (String chatRoomId : user.getJoinedGroup()) {
            ChatRoom chatRoom = chatRoomMap.get(chatRoomId);
            if (chatRoom.isJoined(user)) {
                chatRoom.removeUser(user);
                if (isChatRoomEmpty(chatRoom, user)) {
                    emptyChatroom.add(chatRoom.getGroupName());
                }
            }
        }
        userNameMap.remove(user.getSession());
        userNameToSession.remove(user.getUsername());

        for (String chatRoomId : user.getJoinedGroup()) {
            String message = buildReason(emptyChatroom.contains(chatRoomId), chatRoomMap.get(chatRoomId), user, LEAVE_REASON_CLOSE_CONNECTION);
            chatRoomMap.get(chatRoomId).sendSystemNotification(message);
            if (emptyChatroom.contains(chatRoomId)) {
                chatRoomMap.remove(chatRoomId);
            }
        }
        user.leaveAllGroup();
    }
}
