package com.gobs.components;

import com.artemis.Component;

public class Animation extends Component {
    public enum AnimationType {
        TRANSLATE,
        ROTATE
    }

    private AnimationType type;
    private int duration;

    public AnimationType getType() {
        return type;
    }

    public void setType(AnimationType type) {
        this.type = type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
