package com.invicto.domain;

public enum UserType {
    GUEST ("room_guest"),
    OWNER ("room_owner");

    private final String title;

    UserType(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
