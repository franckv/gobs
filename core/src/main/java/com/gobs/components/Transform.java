package com.gobs.components;

import com.badlogic.ashley.core.Component;

/**
 * Transform the position of an entity
 */
public class Transform implements Component {
    int dx, dy;
    int rotation;

    public Transform() {
        this.dx = 0;
        this.dy = 0;
        this.rotation = 0;
    }

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
