package com.gobs.components;

import com.badlogic.ashley.core.Component;

public class Designation implements Component {
    private int x;
    private int y;
    private int width;
    private int height;

    public Designation(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 1;
        this.height = 1;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void extend(int dx, int dy) {
        this.width += dx;
        this.height += dy;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setDimension(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
