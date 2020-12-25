package com.invicto.domain;

public class User {

    private int id;
    private String login;
    private UserType userType;
    private Room room;
    boolean writePermission;
    boolean drawPermission;

    public User(int id, String login, UserType userType, Room room, boolean writePermission, boolean drawPermission) {
        this.id = id;
        this.login = login;
        this.userType = userType;
        this.room = room;
        this.writePermission = writePermission;
        this.drawPermission = drawPermission;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public boolean isWritePermission() {
        return writePermission;
    }

    public void setWritePermission(boolean writePermission) {
        this.writePermission = writePermission;
    }

    public boolean isDrawPermission() {
        return drawPermission;
    }

    public void setDrawPermission(boolean drawPermission) {
        this.drawPermission = drawPermission;
    }
}
