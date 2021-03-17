package com.mics2_50.chatproject.model;

public class Message {
    private String text;
    private String username;
    private boolean fromUser;

    public Message(String text, String username, boolean fromUser) {
        this.text = text;
        this.username = username;
        this.fromUser = fromUser;
    }

    public String getText() {
        return this.text;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean isFromUser() {
        return this.fromUser;
    }
}
