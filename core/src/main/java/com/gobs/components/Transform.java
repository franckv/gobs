package com.gobs.components;

import com.artemis.Component;

/**
 * Transform the position of an entity
 */
public class Transform extends Component {
    int dx = 0, dy = 0;
    int rotation = 0;

    public int getDX() {
        return dx;
    }

    public int getDY() {
        return dy;
    }

    public int getRotation() {
        return rotation;
    }

    public void setDX(int dx) {
        this.dx = dx;
    }

    public void setDY(int dy) {
        this.dy = dy;
    }

    public void addX(int x) {
        dx += x;
    }

    public void addY(int y) {
        dy += y;
    }

    public void rotate(int rotation) {
        this.rotation = rotation;
    }
}
