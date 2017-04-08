package com.gobs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IntervalIteratingSystem;
import com.gobs.StateManager;
import com.gobs.components.AI;
import com.gobs.components.Animation;

/**
 * Apply a behavior to an AI controlled entity
 */
public class AISystem extends IntervalIteratingSystem {
    private ComponentMapper<AI> am;

    @Wire
    private StateManager stateManager;

    public AISystem(float interval) {
        super(Aspect.all(AI.class).exclude(Animation.class), interval);
    }

    @Override
    public boolean checkProcessing() {
        return stateManager.getState() == StateManager.State.CRAWL && super.checkProcessing();
    }

    @Override
    protected void process(int entityId) {
        AI ai = am.get(entityId);
        ai.getBehavior().update();
    }
}
