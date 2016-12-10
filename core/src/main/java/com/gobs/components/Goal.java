package com.gobs.components;

import com.badlogic.ashley.core.Component;

/**
 * Assign a destination goal to an entity
 */
public class Goal implements Component {
    int x, y;

    public Goal(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
