package com.gobs.components;

import com.badlogic.ashley.core.Component;

public class Progress implements Component {
    private int duration;
    private int progress;

    public Progress(int duration) {
        this.duration = duration;
        this.progress = 0;
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
