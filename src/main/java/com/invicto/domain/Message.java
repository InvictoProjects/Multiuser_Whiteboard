package com.invicto.domain;

import java.time.LocalDateTime;

public class Message {

    private int senderId;
    private LocalDateTime time;
    private String text;

    public Message(int senderId, LocalDateTime time, String text) {
        this.senderId = senderId;
        this.time = time;
        this.text = text;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
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
