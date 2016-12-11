package com.gobs;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.gobs.input.ContextManager;
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
            GameState.getGameState().setScreen(GameState.SCREEN.WORLD);
            GameState.getContextManager().activateContext(ContextManager.ContextType.CRAWLING);
        }

        @Override
        public void exit(GameState state) {
            GameState.getContextManager().disableContext(ContextManager.ContextType.CRAWLING);
        }
    },
    EDITMAP {
        @Override
        public void update(GameState state) {
        }

        @Override
        public void enter(GameState state) {
            GameState.getEngine().getSystem(AISystem.class).setProcessing(false);
            GameState.getGameState().setScreen(GameState.SCREEN.MAP);
            GameState.getContextManager().activateContext(ContextManager.ContextType.EDITMAP);
        }

        @Override
        public void exit(GameState state) {
            GameState.getContextManager().disableContext(ContextManager.ContextType.EDITMAP);
        }
    },
    MAP {
        @Override
        public void update(GameState entity) {
        }

        @Override
        public void enter(GameState state) {
            GameState.getEngine().getSystem(AISystem.class).setProcessing(false);
            GameState.getGameState().setScreen(GameState.SCREEN.MAP);
            GameState.getContextManager().activateContext(ContextManager.ContextType.MAP);

        }

        @Override
        public void exit(GameState state) {
            GameState.getContextManager().disableContext(ContextManager.ContextType.MAP);
        }
    };

    @Override
    public boolean onMessage(GameState state, Telegram telegram) {
        return false;
    }
}
