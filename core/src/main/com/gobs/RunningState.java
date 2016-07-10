package com.gobs;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.gobs.systems.AISystem;

/**
 *
 */
public enum RunningState implements State<GameState> {
    CRAWL {
        @Override
        public void update(GameState state) {
        }

        @Override
        public void enter(GameState state) {
            GameState.getEngine().getSystem(AISystem.class).setProcessing(true);
            GameState.setMainScreen();
        }

    },
    SELECT {
        @Override
        public void update(GameState state) {
        }
        
        @Override
        public void enter(GameState state) {
            GameState.getEngine().getSystem(AISystem.class).setProcessing(false);
            GameState.setMapScreen();
        }
    },
    MAP {
        @Override
        public void update(GameState entity) {
        }
        
        @Override
        public void enter(GameState state) {
            GameState.getEngine().getSystem(AISystem.class).setProcessing(false);
            GameState.setMapScreen();
        }
    };

    @Override
    public void exit(GameState state) {
    }

    @Override
    public boolean onMessage(GameState state, Telegram telegram) {
        return false;
    }
}
