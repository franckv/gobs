package com.gobs.components;

import com.badlogic.ashley.core.Component;
import com.gobs.RunningState;

/**
 * Set the camera to follow this entity
 */
public class Camera implements Component {
    public enum Orientation {UP, DOWN, LEFT, RIGHT};

    RunningState state;
    Orientation orientation;
    
    public Camera(RunningState state, Orientation orientation) {
        this.state = state;
        this.orientation = orientation;
    }

    public RunningState getState() {
        return state;
    }
    
    public Orientation getOrientation() {
        return orientation;
    }
    
    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }
}
