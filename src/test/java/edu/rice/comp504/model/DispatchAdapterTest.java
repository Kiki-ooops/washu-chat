package edu.rice.comp504.model;

import edu.rice.comp504.model.message.Message;
import edu.rice.comp504.model.message.RequestPayload;
import junit.framework.TestCase;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static edu.rice.comp504.model.DataStorage.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class DispatchAdapterTest extends TestCase {
    public void testCreateUser() {
        Session userSess1 = mock(Session.class);
        RemoteEndpoint endpoint = mock(RemoteEndpoint.class);
        try {
            doReturn(endpoint).when(userSess1).getRemote();
            doNothing().when(endpoint).sendString(anyString());
        } catch (IOException e) {
            e.printStackTrace();
        }

//        initial chat app
        DataStorage.initDataSource();
        DispatchAdapter da = DispatchAdapter.getInstance();
        assertEquals("there is no user", 0, userNameMap.size());

//        simulate create user
        // Error test
        String payload = "{\"username\":\"Ian\", \"age\":20, \"location\":\"na\", \"school\":\"rice\", \"joinedGroup\":[]}";
        da.updateProfile(userSess1, payload);
        assertEquals("there is no user", 0, userNameMap.size());

        // Success test
        da.createConnection(userSess1);
        payload = "{\"username\":\"Ian\", \"age\":20, \"location\":\"na\", \"school\":\"rice\", \"joinedGroup\":[]}";
        da.updateProfile(userSess1, payload);
        assertEquals("there is one user", 1, userNameMap.size());
        // to string
        User user1 = userNameMap.get(userSess1);
        System.out.println(userNameMap.get(userSess1));
        user1.setUsername("IanF");
        user1.setAge(23);
        user1.setJoinedGroup(new ArrayList<>());
        user1.setSession(userSess1);
        user1.setLocation("as");
        user1.setSchool("rice");
        System.out.println(userNameMap.get(userSess1));


        // close sessison
        da.closeConnection(userSess1);
        assertEquals("there is no user", 0, userNameMap.size());

    }

    public void testCreateRoom() {
        Session userSess1 = mock(Session.class);
        RemoteEndpoint endpoint = mock(RemoteEndpoint.class);
        try {
            doReturn(endpoint).when(userSess1).getRemote();
            doNothing().when(endpoint).sendString(anyString());
        } catch (IOException e) {
            e.printStackTrace();
        }

//        initial chat app
        DataStorage.initDataSource();
        DispatchAdapter da = DispatchAdapter.getInstance();
        assertEquals("there is no chatroom", 0, chatRoomMap.size());

//        simulate create user
        da.createConnection(userSess1);
        String payload = "{\"username\":\"zhaokangpan\", \"age\":20, \"location\":\"na\", \"school\":\"rice\", \"joinedGroup\":[]}";
        da.updateProfile(userSess1, payload);

//        simulate create room
        payload = "{\"groupName\":\"hello\",\"owner\":\"zhaokangpan\",\"minAge\":10,\"maxAge\":30,\"locations\":[\"na\"],\"schools\":[\"rice\"]}";
        assertEquals("success code", 200, da.creatChatRoom(payload));
        assertEquals("there is one chatroom", 1, chatRoomMap.size());
        System.out.println(chatRoomMap.get("hello"));

        // create same room
        assertEquals("error code", 409, da.creatChatRoom(payload));
        assertEquals("there is one chatroom", 1, chatRoomMap.size());
    }

    public void testJoinAndLeaveChatRoom() {

        Session userSess1 = mock(Session.class);
        Session userSess2 = mock(Session.class);
        RemoteEndpoint endpoint = mock(RemoteEndpoint.class);
        try {
            doReturn(endpoint).when(userSess1).getRemote();
            doReturn(endpoint).when(userSess2).getRemote();
            doNothing().when(endpoint).sendString(anyString());
        } catch (IOException e) {
            e.printStackTrace();
        }

//        initial chat app
        DataStorage.initDataSource();
        DispatchAdapter da = DispatchAdapter.getInstance();
        assertEquals("there is no chatroom", 0, chatRoomMap.size());

//        simulate create users
        da.createConnection(userSess1);
        String payload = "{\"username\":\"zhaokangpan\", \"age\":20, \"location\":\"na\", \"school\":\"rice\", \"joinedGroup\":[]}";
        da.updateProfile(userSess1, payload);

        // user 2
        da.createConnection(userSess2);
        payload = "{\"username\":\"Ian\", \"age\":40, \"location\":\"na\", \"school\":\"rice\", \"joinedGroup\":[]}";
        da.updateProfile(userSess2, payload);
        User user2 = userNameMap.get(userSess2);

//        simulate create room
        payload = "{\"groupName\":\"hello\",\"owner\":\"zhaokangpan\",\"minAge\":10,\"maxAge\":30,\"locations\":[\"na\"],\"schools\":[\"rice\"]}";
        da.creatChatRoom(payload);
        // creat another chatroom
        payload = "{\"groupName\":\"Comp504\",\"owner\":\"Ian\",\"minAge\":10,\"maxAge\":30,\"locations\":[\"na\"],\"schools\":[\"rice\"]}";
        da.creatChatRoom(payload);

        // simulate join room
        payload = "{\"username\":\"Ian\", \"groupName\":\"hello\"}";
        assertEquals("error code", 403, da.joinChatRoom(payload));

        // meet restriction
        user2.setAge(20);
        assertEquals("success code", 200, da.joinChatRoom(payload));
        System.out.println(chatRoomMap.get("hello").getJoinedUsers().size());
        System.out.println(chatRoomMap.get("hello").deepCopyCut().getJoinedUsers().size());

        // join repeatedly
        assertEquals("joined error code", 403, da.joinChatRoom(payload));

        // simulate leave room
        assertEquals("success code", 200, da.leaveChatRoom(payload));
        assertEquals("is left error code", 403, da.leaveChatRoom(payload));

        // Ian join again
        da.joinChatRoom(payload);

        // test Ian leave as owner
        payload = "{\"username\":\"zhaokangpan\", \"groupName\":\"Comp504\"}";
        da.joinChatRoom(payload);
        payload = "{\"username\":\"Ian\", \"groupName\":\"Comp504\"}";
        da.leaveChatRoom(payload);

        // test Ian leave all room
        // creat another chatroom
        payload = "{\"groupName\":\"Comp504\",\"owner\":\"Ian\",\"minAge\":10,\"maxAge\":30,\"locations\":[\"na\"],\"schools\":[\"rice\"]}";
        da.creatChatRoom(payload);
        payload = "{\"username\":\"Ian\", \"groupName\":\"Comp504\"}";
        System.out.println(da.getList(payload));
        da.closeConnection(userSess2);

        // owner leave room
        payload = "{\"username\":\"zhaokangpan\", \"groupName\":\"hello\"}";
//        System.out.println(userNameMap.get(userSess1));
        assertEquals("success code", 200, da.leaveChatRoom(payload));
        assertEquals("there is no chatroom", 0, chatRoomMap.size());

    }

    public void testSendMessage() {
        Session userSess1 = mock(Session.class);
        Session userSess2 = mock(Session.class);
        RemoteEndpoint endpoint = mock(RemoteEndpoint.class);
        try {
            doReturn(endpoint).when(userSess1).getRemote();
            doReturn(endpoint).when(userSess2).getRemote();
            doNothing().when(endpoint).sendString(anyString());
        } catch (IOException e) {
            e.printStackTrace();
        }

//        initial chat app
        DataStorage.initDataSource();
        DispatchAdapter da = DispatchAdapter.getInstance();
        assertEquals("there is no chatroom", 0, chatRoomMap.size());

//        simulate create users
        da.createConnection(userSess1);
        String payload = "{\"username\":\"zhaokangpan\", \"age\":20, \"location\":\"na\", \"school\":\"rice\", \"joinedGroup\":[]}";
        da.updateProfile(userSess1, payload);

        // user 2
        da.createConnection(userSess2);
        payload = "{\"username\":\"Ian\", \"age\":20, \"location\":\"na\", \"school\":\"rice\", \"joinedGroup\":[]}";
        da.updateProfile(userSess2, payload);
        User user2 = userNameMap.get(userSess2);

//        simulate create room
        payload = "{\"groupName\":\"hello\",\"owner\":\"zhaokangpan\",\"minAge\":10,\"maxAge\":30,\"locations\":[\"na\"],\"schools\":[\"rice\"]}";
        da.creatChatRoom(payload);
        // creat another chatroom
        payload = "{\"groupName\":\"Comp504\",\"owner\":\"Ian\",\"minAge\":10,\"maxAge\":30,\"locations\":[\"na\"],\"schools\":[\"rice\"]}";
        da.creatChatRoom(payload);

        // simulate join room
//        System.out.println(user2.getAge());
        payload = "{\"username\":\"Ian\", \"groupName\":\"hello\"}";
        assertEquals("success code", 200, da.joinChatRoom(payload));

        // simulate send personal message
        payload = "{\"type\":\"ONE\", \"sender\":\"Ian\", \"receiver\":\"zhaokangpan\", \"text\":\"how are you\", \"groupName\":\"hello\"}";
        Message m = PayloadParser.createMessage(payload);
        System.out.println(m);
        da.sendMessage(m);

        // simulate send system message
        payload = "{\"type\":\"all\", \"sender\":\"zhaokangpan\", \"receiver\":\"Ian\", \"text\":\"504 due on Wednesday\", \"groupName\":\"hello\"}";
        Message a = PayloadParser.createMessage(payload);
        System.out.println(a);
        da.sendMessage(a);

        // simulate send hate message
        payload = "{\"type\":\"ONE\", \"sender\":\"Ian\", \"receiver\":\"zhaokangpan\", \"text\":\"I hate you\", \"groupName\":\"hello\"}";
        Message h = PayloadParser.createMessage(payload);
        System.out.println(h);
        da.sendMessage(h);

    }

    public void testClass() {

//        test Message
        String payload = "{\"type\":\"ONE\", \"sender\":\"Ian\", \"receiver\":\"zhaokangpan\", \"text\":\"I hate you\", \"groupName\":\"hello\"}";
        Message h = PayloadParser.createMessage(payload);
        System.out.println(h);

        // test set message
        h.setGroupName("hi");
        h.setReceiver("zhaokang");
        h.setSender("IanF");
        h.setText("no more hate");
        h.setType("all");
        System.out.println(h);

//        test ChatRoom
        ChatRoom chatRoom = new ChatRoom("Comp504");
        chatRoom.setJoinedUsers(chatRoom.getJoinedUsers());


//        test Status
        Status status = new Status(200);
        System.out.println(status.getStatus());
        status.setStatus(403);
        System.out.println(status.getStatus());


//        test Request Payload
        RequestPayload requestPayload = new RequestPayload();
        requestPayload.setGroupName("hey");
        requestPayload.setUsername("Ian");
        System.out.println(requestPayload);
    }

}
