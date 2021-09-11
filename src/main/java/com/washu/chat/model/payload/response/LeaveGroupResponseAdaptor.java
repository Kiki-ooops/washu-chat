package com.washu.chat.model.payload.response;

import com.google.gson.Gson;
import com.washu.chat.model.message.Message;

import static com.washu.chat.model.constant.Constant.*;

/**
 * get a json representation for responses the data
 */
public class LeaveGroupResponseAdaptor implements ResponseAdapter {
    private String leaver;
    private String groupName;
    private String reason;

    /**
     * Constructor.
     * @param leaver the person who leaves the chatRoom
     * @param groupName the name of the chatRoom
     * @param reason the leaving reason
     */
    public LeaveGroupResponseAdaptor(String leaver, String groupName, String reason) {
        this.leaver = leaver;
        this.groupName = groupName;
        this.reason = reason;
    }

    /**
     * Get the serialized json represented data.
     * @param gson an Gson instance.
     * @return the serialized data object
     */
    public String getJsonRepresentation(Gson gson) {
        Message message =
                new Message(
                        SYSTEM,
                        leaver,
                        EMPTY,
                        String.format(LEAVE_GROUP_NOTIFICATION, leaver, groupName, reason),
                        groupName
                );
        return gson.toJson(message);
    }
}
