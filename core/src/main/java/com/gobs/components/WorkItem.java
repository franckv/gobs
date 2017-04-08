package com.gobs.components;

import com.badlogic.ashley.core.Component;

public class WorkItem implements Component {
    public enum WorkType {
        DIGGING,
        FILLING
    }

    private WorkType type;
    private int duration;

    public WorkItem(WorkType type, int duration) {
        this.type = type;
        this.duration = duration;
    }

    public WorkType getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }
}
