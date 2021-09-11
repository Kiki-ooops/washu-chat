package edu.rice.comp504.model;

import edu.rice.comp504.model.message.Message;
import edu.rice.comp504.model.payload.response.MessageSendAdaptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static edu.rice.comp504.model.constant.Constant.ALL;
import static edu.rice.comp504.model.constant.Constant.gson;
import static edu.rice.comp504.model.DataStorage.*;

/**
 * The class for chatroom
 */
public class ChatRoom {
    private String groupName;
    private String owner;
    private int minAge;
    private int maxAge;
    private Set<String> locations;
    private Set<String> schools;
    private List<User> joinedUsers;

    public ChatRoom() {
        joinedUsers = new ArrayList<>();
    }

    public boolean isOwner(User user) {
        return owner.equals(user.getUsername());
    }

    public boolean isEmpty() {
        return joinedUsers.size() == 0;
    }

    public void addUser(User user) {
        joinedUsers.add(user);
    }

    public void removeUser(User user) {
        joinedUsers.remove(user);
    }

    public boolean isJoined(User user) {
        return joinedUsers.contains(user);
    }

    public void sendSystemNotification(String message) {
        for (User receiver : userNameMap.values()) {
            try {
                receiver.getSession().getRemote().sendString(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void notifyUser(Message message, User sender) {
        if (message.getType().equals(ALL)) {
            for (User receiver : joinedUsers) {
                if (receiver != sender) {
                    try {
                        receiver
                                .getSession()
                                .getRemote()
                                .sendString(
                                        new MessageSendAdaptor(message)
                                                .getJsonRepresentation(gson)
                                );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            for (User receiver : joinedUsers) {
                if (receiver.getUsername().equals(message.getReceiver())) {
                    try {
                        receiver
                                .getSession()
                                .getRemote()
                                .sendString(
                                        new MessageSendAdaptor(message)
                                                .getJsonRepresentation(gson)
                                );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public ChatRoom(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public Set<String> getLocations() {
        return locations;
    }

    public void setLocations(Set<String> locations) {
        this.locations = locations;
    }

    public Set<String> getSchools() {
        return schools;
    }

    public void setSchools(Set<String> schools) {
        this.schools = schools;
    }

    public List<User> getJoinedUsers() {
        return joinedUsers;
    }

    public void setJoinedUsers(List<User> joinedUsers) {
        this.joinedUsers = joinedUsers;
    }

    public ChatRoom deepCopyCut() {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setOwner(owner);
        chatRoom.setGroupName(groupName);
        chatRoom.setLocations(locations);
        chatRoom.setSchools(schools);
        chatRoom.setMinAge(minAge);
        chatRoom.setMaxAge(maxAge);
        for (User user : joinedUsers) {
            User copyUser = new User(null);
            copyUser.setUsername(user.getUsername());
            chatRoom.addUser(copyUser);
        }
        return chatRoom;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "groupName='" + groupName + '\'' +
                ", owner='" + owner + '\'' +
                ", minAge=" + minAge +
                ", maxAge=" + maxAge +
                ", locations=" + locations +
                ", schools=" + schools +
                ", joinedUsers=" + joinedUsers +
                '}';
    }
}
