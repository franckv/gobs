package com.gobs.components;

import com.badlogic.ashley.core.Component;
import com.gobs.ai.AIBehavior;

/**
 * Link a behavior to an entity
 */
public class AI implements Component {
    AIBehavior behavior;

    public AI(AIBehavior behavior) {
        this.behavior = behavior;
    }

    public AIBehavior getBehavior() {
        return behavior;
    }
}
