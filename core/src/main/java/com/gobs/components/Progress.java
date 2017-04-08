package com.gobs.components;

import com.artemis.Component;

public class Progress extends Component {
    private int duration = 0;
    private int progress = 0;

    public void setDuration(int duration) {
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
