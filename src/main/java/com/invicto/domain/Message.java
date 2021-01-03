package com.invicto.domain;

import java.time.LocalDateTime;

public class Message {

    private Integer id;
    private String roomId;
    private User sender;
    private LocalDateTime time;
    private String text;

    public Message(Integer id, String roomId, User sender, LocalDateTime time, String text) {
        this.id = id;
        this.roomId = roomId;
        this.sender = sender;
        this.time = time;
        this.text = text;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
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
