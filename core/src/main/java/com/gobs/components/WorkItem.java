package com.gobs.components;

import com.artemis.Component;

public class WorkItem extends Component {
    public enum WorkType {
        DIGGING,
        FILLING
    }

    private WorkType type;
    private int duration;

    public WorkType getType() {
        return type;
    }

    public void setType(WorkType type) {
        this.type = type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
