package com.invicto.domain;

import java.time.LocalDateTime;

public class Message {

    private User sender;
    private LocalDateTime time;
    private String text;

    public Message(User sender, LocalDateTime time, String text) {
        this.sender = sender;
        this.time = time;
        this.text = text;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
