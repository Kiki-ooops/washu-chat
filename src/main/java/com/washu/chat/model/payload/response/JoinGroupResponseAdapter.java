package com.washu.chat.model.payload.response;

import com.google.gson.Gson;
import com.washu.chat.model.message.Message;

import static com.washu.chat.model.constant.Constant.*;

/**
 * get a json representation for responses the data
 */
public class JoinGroupResponseAdapter implements ResponseAdapter {

    private String participant;
    private String groupName;

    /**
     * Constructor.
     * @param participant the participant
     * @param groupName the name of the chatRoom
     */
    public JoinGroupResponseAdapter(String participant, String groupName) {
        this.participant = participant;
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
                        participant,
                        EMPTY,
                        String.format(JOIN_GROUP_NOTIFICATION, participant, groupName),
                        groupName);
        return gson.toJson(message);
    }
}
