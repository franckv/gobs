package com.gobs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.gobs.GobsEngine;
import com.gobs.components.AI;
import com.gobs.components.Animation;

/**
 * Apply a behavior to an AI controlled entity
 */
public class AISystem extends IntervalIteratingSystem {
    private ComponentMapper<AI> am = ComponentMapper.getFor(AI.class);

    public AISystem(float interval) {
        this(interval, 0);
    }

    public AISystem(float interval, int priority) {
        super(Family.all(AI.class).exclude(Animation.class).get(), interval, priority);
    }

    @Override
    public boolean checkProcessing() {
        return !((GobsEngine) getEngine()).isRendering() && super.checkProcessing();
    }

    @Override
    protected void processEntity(Entity entity) {
        AI ai = am.get(entity);
        ai.getBehavior().update();
    }
}
