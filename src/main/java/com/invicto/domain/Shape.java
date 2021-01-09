package com.invicto.domain;

public class Shape {

    private String roomId;
    private String path;
    private int thickness;
    private boolean dotted;
    private boolean filled;
    private String color;

    public Shape(String roomId, String path, int thickness, boolean dotted, boolean filled, String color) {
        this.roomId = roomId;
        this.path = path;
        this.thickness = thickness;
        this.dotted = dotted;
        this.filled = filled;
        this.color = color;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    public boolean isDotted() {
        return dotted;
    }

    public void setDotted(boolean dotted) {
        this.dotted = dotted;
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
