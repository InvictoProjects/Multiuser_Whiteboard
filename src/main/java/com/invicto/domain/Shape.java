package com.invicto.domain;

public class Shape {

    private String color;
    private int thickness;
    private boolean dotted;
    private boolean filled;

    public Shape(String color, int thickness, boolean dotted, boolean filled) {
        this.color = color;
        this.thickness = thickness;
        this.dotted = dotted;
        this.filled = filled;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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
}
