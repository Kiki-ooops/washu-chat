package com.washu.chat.model.payload.response;

import com.google.gson.Gson;
import com.washu.chat.model.message.Message;

import static com.washu.chat.model.constant.Constant.*;

/**
 * get a json representation for responses the data
 */
public class CreateGroupResponseAdaptor implements ResponseAdapter {
    private String creator;
    private String groupName;

    /**
     * The constructor.
     * @param creator the creator of the chatRoom
     * @param groupName the name of the chatRoom
     */
    public CreateGroupResponseAdaptor(String creator, String groupName) {
        this.creator = creator;
        this.groupName = groupName;
    }

    /**
     * Get the serialized json represented data.
     * @param gson an Gson instance.
     * @return the serialized data object
     */
    public String getJsonRepresentation(Gson gson) {
        Message message = new Message(
                SYSTEM,
                creator,
                EMPTY,
                String.format(CREATE_GROUP_NOTIFICATION, creator, groupName),
                groupName
        );
        return gson.toJson(message);
    }
}
