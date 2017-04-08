package com.gobs.components;

import com.badlogic.ashley.core.Component;

public class Animation implements Component {
    public enum AnimationType {
        TRANSLATE,
        ROTATE
    }

    private AnimationType type;
    private int duration;

    public Animation(AnimationType type, int duration) {
        this.type = type;
        this.duration = duration;
    }

    public AnimationType getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }
}
