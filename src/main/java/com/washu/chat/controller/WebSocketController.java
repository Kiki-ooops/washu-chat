package com.washu.chat.controller;

import com.washu.chat.model.message.Message;
import com.washu.chat.model.DispatchAdapter;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import static com.washu.chat.model.constant.Constant.REGISTER;
import static com.washu.chat.model.PayloadParser.createMessage;

/**
 * Create a web socket for the server.
 */
@WebSocket
public class WebSocketController {

    /**
     * Open user's session.
     *
     * @param user The user whose session is opened.
     */
    @OnWebSocketConnect
    public void onConnect(Session user) {
        DispatchAdapter.getInstance().createConnection(user);
    }

    /**
     * Close the user's session.
     *
     * @param user The use whose session is closed.
     */
    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        DispatchAdapter.getInstance().closeConnection(user);
    }

    /**
     * Send a message.
     *
     * @param user    The session user sending the message.
     * @param message The message to be sent.
     */
    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        Message msgObj = createMessage(message);
        if (msgObj.getType().equals(REGISTER)) {
            DispatchAdapter.getInstance().updateProfile(user, msgObj.getText());
        } else {
            DispatchAdapter.getInstance().sendMessage(msgObj);
        }
    }
}
