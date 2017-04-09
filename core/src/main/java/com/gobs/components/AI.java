package com.gobs.components;

import com.artemis.Component;
import com.artemis.annotations.Transient;
import com.gobs.ai.AIBehavior;

/**
 * Link a behavior to an entity
 */
@Transient
public class AI extends Component {
    private AIBehavior behavior;

    public AIBehavior getBehavior() {
        return behavior;
    }

    public void setBehavior(AIBehavior behavior) {
        this.behavior = behavior;
    }
}
