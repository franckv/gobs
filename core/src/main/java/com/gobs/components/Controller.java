package com.gobs.components;

import com.badlogic.ashley.core.Component;
import com.gobs.RunningState;

/**
 * Make an entity controllable by the player
 */
public class Controller implements Component {
    RunningState state;

    public Controller(RunningState state) {
        this.state = state;
    }

    public RunningState getState() {
        return state;
    }
}
