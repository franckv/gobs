package com.gobs.components;

import com.artemis.Component;

public class Designation extends Component {
    private int x;
    private int y;
    private int width = 1;
    private int height = 1;

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
