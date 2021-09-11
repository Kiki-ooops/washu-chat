package edu.rice.comp504.model.payload.response;

import com.google.gson.Gson;
import edu.rice.comp504.model.message.Message;

/**
 * get a json representation for responses the data
 */
public class MessageSendAdaptor implements ResponseAdapter {

    private Message message;

    /**
     * Constructor.
     * @param message the sending message
     */
    public MessageSendAdaptor(Message message) {
        this.message = message;
    }

    /**
     * Get the serialized json represented data.
     * @param gson an Gson instance.
     * @return the serialized data object
     */
    public String getJsonRepresentation(Gson gson) {
        return gson.toJson(message);
    }
}
