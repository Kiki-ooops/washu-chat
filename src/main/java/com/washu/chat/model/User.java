package com.washu.chat.model;

import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private int age;
    private String location;
    private String school;
    private Session session;
    private List<String> joinedGroup;

    public User(Session session) {
        this.session = session;
        this.joinedGroup = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public List<String> getJoinedGroup() {
        return joinedGroup;
    }

    public void setJoinedGroup(List<String> joinedGroup) {
        this.joinedGroup = joinedGroup;
    }

    public void joinGroup(String chatRoomId) {
        joinedGroup.add(chatRoomId);
    }

    public void leaveAllGroup() {
        joinedGroup.clear();
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", age=" + age +
                ", location='" + location + '\'' +
                ", school='" + school + '\'' +
                ", joinedGroup=" + joinedGroup +
                '}';
    }
}
