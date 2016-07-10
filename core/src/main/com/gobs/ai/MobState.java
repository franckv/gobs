package com.gobs.ai;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

public enum MobState implements State<MobBehavior> {
    MOVING() {
        @Override
        public void update(MobBehavior behavior) {
            behavior.isMoving();
        }
    },
    WAITING() {
        @Override
        public void update(MobBehavior behavior) {
            behavior.isWaiting();
        }
    },
    CHASING() {
        @Override
        public void update(MobBehavior behavior) {
            behavior.isChasing();
        }
    };

    @Override
    public void enter(MobBehavior entity) {
    }

    @Override
    public void exit(MobBehavior entity) {
    }

    @Override
    public boolean onMessage(MobBehavior entity, Telegram telegram) {
        return false;
    }
}
