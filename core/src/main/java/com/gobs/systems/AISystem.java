package com.gobs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.gobs.components.AI;

/**
 * Apply a behavior to an AI controlled entity
 */
public class AISystem extends EntityProcessingSystem {

    private float interval;
    private float accumulator;

    private ComponentMapper<AI> am = ComponentMapper.getFor(AI.class);

    public AISystem(float interval) {
        this(interval, 0);
    }

    public AISystem(float interval, int priority) {
        super(Family.all(AI.class).get(), priority);
        this.interval = interval;
    }

    @Override
    public void update(float deltaTime) {
        accumulator += deltaTime;

        while (accumulator >= interval) {
            accumulator -= interval;
            updateInterval();
        }
    }

    private void updateInterval() {
        for (Entity entity : getEntities()) {
            AI ai = am.get(entity);
            ai.getBehavior().update();
        }
    }

    @Override
    public void dispose() {
    }
}
