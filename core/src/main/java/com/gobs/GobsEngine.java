package com.gobs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

public class GobsEngine extends Engine {
    private boolean rendering = false;
    
    public boolean isRendering() {
        return rendering;
    }

    public void addSystem(EntitySystem system, boolean processing) {
        system.setProcessing(processing);
        super.addSystem(system);
    }
    
    public void update(float deltaTime, boolean rendering) {
        this.rendering = rendering;
        super.update(deltaTime);
    }
}
