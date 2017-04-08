package com.gobs.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.gobs.GobsEngine;

public abstract class LogicSystem extends EntitySystem {
    public LogicSystem() {
        this(0);
    }

    public LogicSystem(int priority) {
        super(priority);
    }

    @Override
    public boolean checkProcessing() {
        return !((GobsEngine) getEngine()).isRendering() && super.checkProcessing();
    }

}
