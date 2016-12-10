package com.gobs.components;

import com.badlogic.ashley.core.Component;

/**
 * Assign a position to an entity
 */
public class Position implements Component {
    int x, y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void translate(int dx, int dy) {
        x += dx;
        y += dy;
    }

    @Override
    public String toString() {
        return "[" + x + "," + y + "]";
    }
    
    
}
