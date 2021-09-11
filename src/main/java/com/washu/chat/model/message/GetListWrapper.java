package com.washu.chat.model.message;

import com.washu.chat.model.ChatRoom;

import java.util.ArrayList;
import java.util.List;

/**
 * Separate chat rooms into different group for offering convenience to render the page on the frontend.
 */
public class GetListWrapper {
    private List<ChatRoom> owner;
    private List<ChatRoom> member;
    private List<ChatRoom> other;

    /**
     * the constructor.
     * @param owner the list of chat rooms that the user belongs to as an owner
     * @param member the list of chat rooms that the user belongs to as a member
     * @param other the list of chat rooms that the user does not belong to
     */
    public GetListWrapper(List<ChatRoom> owner, List<ChatRoom> member, List<ChatRoom> other) {
        this.owner = deepCopyCut(owner);
        this.member = deepCopyCut(member);
        this.other = deepCopyCut(other);
    }

    /**
     * deep copy the chat room list which is used for resolving mutual reference problem.
     * @param list the list of chat room
     * @return the deep copied list of chat room
     */
    private List<ChatRoom> deepCopyCut(List<ChatRoom> list) {
        List<ChatRoom> resList = new ArrayList<>();
        for (ChatRoom chatRoom : list) {
            resList.add(chatRoom.deepCopyCut());
        }
        return resList;
    }
}
