package com.gobs;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.gobs.input.ContextManager;
import com.gobs.systems.AISystem;
import com.gobs.systems.FPVRenderingSystem;
import com.gobs.systems.MapRenderingSystem;

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
            GameState.getEngine().getSystem(FPVRenderingSystem.class).setProcessing(true);
            GameState.getContextManager().activateContext(ContextManager.ContextType.CRAWLING);
            GameState.getMapLayer().setDirty(true);
        }

        @Override
        public void exit(GameState state) {
            GameState.getEngine().getSystem(FPVRenderingSystem.class).setProcessing(false);
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
            GameState.getEngine().getSystem(MapRenderingSystem.class).setProcessing(true);
            GameState.getContextManager().activateContext(ContextManager.ContextType.EDITMAP);
            GameState.getMapLayer().setDirty(true);
        }

        @Override
        public void exit(GameState state) {
            GameState.getEngine().getSystem(MapRenderingSystem.class).setProcessing(false);
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
            GameState.getEngine().getSystem(MapRenderingSystem.class).setProcessing(true);
            GameState.getContextManager().activateContext(ContextManager.ContextType.MAP);
            GameState.getMapLayer().setDirty(true);
        }

        @Override
        public void exit(GameState state) {
            GameState.getEngine().getSystem(MapRenderingSystem.class).setProcessing(false);
            GameState.getContextManager().disableContext(ContextManager.ContextType.MAP);
        }
    };

    @Override
    public boolean onMessage(GameState state, Telegram telegram) {
        return false;
    }
}
