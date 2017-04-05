package com.gobs.components;

import com.badlogic.ashley.core.Component;

/**
 * Set the camera to follow this entity
 */
public class Camera implements Component {
    public enum Orientation {
        UP, DOWN, LEFT, RIGHT
    };

    Orientation orientation;
    // angle starting from current orientation
    int angle;

    public Camera(Orientation orientation) {
        this.orientation = orientation;
        this.angle = 0;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
        this.angle = 0;
    }
    
    public void setAngle(int angle) {
        this.angle = angle;
    }
    
    public int getAngle() {
        return angle;
    }

    void setOrientation(int rotation) {
        while (rotation < 0) {
            rotation += 360;
        }
        switch (rotation % 360) {
            case 0:
                orientation = Orientation.UP;
                break;
            case 90:
                orientation = Orientation.RIGHT;
                break;
            case 180:
                orientation = Orientation.DOWN;
                break;
            case 270:
                orientation = Orientation.LEFT;
                break;
        }
        this.angle = 0;
    }

    public int getRotation() {
        switch (orientation) {
            case UP:
                return 0;
            case DOWN:
                return 180;
            case RIGHT:
                return 90;
            case LEFT:
                return 270;
        }

        return 0;
    }

    public void rotate(int rotation) {
        setOrientation(getRotation() + rotation);
    }
}
