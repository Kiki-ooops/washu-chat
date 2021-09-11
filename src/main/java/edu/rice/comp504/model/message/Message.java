package edu.rice.comp504.model.message;

/**
 * This message class is used as data wrapper for web socket.
 */
public class Message {
    private String type;
    private String sender;
    private String receiver;
    private String text;
    private String groupName;

    /**
     * The constructor.
     * @param type the type of the message (system, all, one)
     * @param sender the sender
     * @param receiver the receiver
     * @param text the content of the message
     * @param groupName the name of the chat rooms
     */
    public Message(String type, String sender, String receiver, String text, String groupName) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.text = text;
        this.groupName = groupName;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type='" + type + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", text='" + text + '\'' +
                ", groupName='" + groupName + '\'' +
                '}';
    }
}
