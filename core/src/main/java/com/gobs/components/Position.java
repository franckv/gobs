package com.gobs.components;

import com.badlogic.ashley.core.Component;

/**
 * Assign a position to an entity
 */
public class Position implements Component {
    int x, y;
    // intermediary positions used for animation
    float dx, dy;

    public Position(int x, int y) {
        this.x = x;
        this.dx = 0;
        this.y = y;
        this.dy = 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public float getDX() {
        return dx;
    }

    public float getDY() {
        return dy;
    }

    public void setDX(float dx) {
        this.dx = dx;
    }

    public void setDY(float dy) {
        this.dy = dy;
    }

    public void translate(int dx, int dy) {
        this.x += dx;
        this.dx = 0;
        this.y += dy;
        this.dy = 0;
    }

    @Override
    public String toString() {
        return "[" + x + "," + y + "]";
    }

}
