package com.gobs.components;

import com.badlogic.ashley.core.Component;

/**
 * Make an entity controllable by the player
 */
public class Controller implements Component {
    boolean active;
    
    public Controller(boolean active) {
        this.active = active;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
}
