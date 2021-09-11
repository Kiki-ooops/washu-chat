package com.washu.chat.model.payload.response;

import com.google.gson.Gson;
import com.washu.chat.model.message.Message;

import static com.washu.chat.model.constant.Constant.LEAVE_REASON_GROUP_DISSOLVED;
import static com.washu.chat.model.constant.Constant.SYSTEM;

/**
 * get a json representation for responses the data
 */
public class DissolveGroupResponseAdaptor implements ResponseAdapter {
    private String groupName;

    /**
     * Constructor.
     * @param groupName the name of the chatRoom.
     */
    public DissolveGroupResponseAdaptor(String groupName) {
        this.groupName = groupName;
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
                        "",
                        "",
                        String.format(LEAVE_REASON_GROUP_DISSOLVED, groupName),
                        groupName
                );
        return gson.toJson(message);
    }
}
