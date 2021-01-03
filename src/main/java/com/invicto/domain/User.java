package com.invicto.domain;

public class User {

    private Integer id;
    private String login;
    private String roomId;
    private UserType userType;
    private boolean writePermission;
    private boolean drawPermission;

    public User(Integer id, String login, String roomId, UserType userType, boolean writePermission, boolean drawPermission) {
        this.id = id;
        this.login = login;
        this.roomId = roomId;
        this.userType = userType;
        this.writePermission = writePermission;
        this.drawPermission = drawPermission;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
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
