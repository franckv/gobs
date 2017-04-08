package com.gobs.components;

import com.artemis.Component;

/**
 * Assign a destination goal to an entity
 */
public class Goal extends Component {
    int x, y;

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
