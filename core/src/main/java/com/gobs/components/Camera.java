package com.gobs.components;

import com.badlogic.ashley.core.Component;

/**
 * Set the camera to follow this entity
 */
public class Camera implements Component {
    public enum Orientation {UP, DOWN, LEFT, RIGHT};

    Orientation orientation;
    
    public Camera(Orientation orientation) {
        this.orientation = orientation;
    }

    public Orientation getOrientation() {
        return orientation;
    }
    
    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }
}
