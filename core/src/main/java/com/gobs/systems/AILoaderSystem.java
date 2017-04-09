package com.gobs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.gobs.ai.AIBehavior;
import com.gobs.ai.MobBehavior;
import com.gobs.components.AI;
import com.gobs.components.AIRef;

public class AILoaderSystem extends IteratingSystem {
    private ComponentMapper<AIRef> rm;
    private ComponentMapper<AI> am;

    public AILoaderSystem() {
        super(Aspect.all(AIRef.class).exclude(AI.class));
    }

    @Override
    protected void process(int entityId) {
        AIRef ref = rm.get(entityId);

        switch (ref.getBehavior()) {
            case "MobBehavior":
                AI ai = am.create(entityId);
                AIBehavior behavior = new MobBehavior(entityId);
                world.inject(behavior);
                ai.setBehavior(behavior);
                break;
        }
    }
}
