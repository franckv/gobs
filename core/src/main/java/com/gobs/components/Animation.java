package com.gobs.components;

import com.badlogic.ashley.core.Component;

public class Animation implements Component {
    public enum AnimationType {
        TRANSLATE,
        ROTATE
    }

    private AnimationType type;
    private int duration;
    private int progress;

    public Animation(AnimationType type, int duration) {
        this.type = type;
        this.duration = duration;
        this.progress = 0;
    }

    public AnimationType getType() {
        return type;
    }
    
    public float getCompletion() {
        return (float) progress / duration;
    }

    public void advance() {
        if (progress < duration) {
            progress++;
        }
    }

    public boolean isComplete() {
        return progress == duration;
    }
}
