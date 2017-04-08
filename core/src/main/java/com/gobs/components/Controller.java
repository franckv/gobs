package com.gobs.components;

import com.artemis.Component;

/**
 * Make an entity controllable by the player
 */
public class Controller extends Component {
    private boolean active;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
