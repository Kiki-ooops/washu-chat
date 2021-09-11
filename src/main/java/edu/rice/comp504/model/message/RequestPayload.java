package edu.rice.comp504.model.message;

/**
 * This class is used for taking the value of parsed parameters from the request.
 */
public class RequestPayload {

    private String username;
    private String groupName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "RequestPayload{" +
                "username='" + username + '\'' +
                ", groupName='" + groupName + '\'' +
                '}';
    }
}
