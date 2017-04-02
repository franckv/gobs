package com.gobs;

import com.badlogic.ashley.core.Engine;
import com.gobs.input.ContextManager;
import com.gobs.map.Layer;
import com.gobs.systems.AISystem;
import com.gobs.systems.FPVRenderingSystem;
import com.gobs.systems.MapRenderingSystem;

public class StateManager {
    public enum State {
        CRAWL,
        EDITMAP,
        MAP
    }

    private State currentState;
    private Engine engine;
    private ContextManager contextManager;
    private Layer mapLayer;

    public StateManager(Engine engine, ContextManager contextManager, Layer mapLayer, State state) {
        this.engine = engine;
        this.contextManager = contextManager;
        this.mapLayer = mapLayer;
        this.currentState = state;
    }

    public void setState(State state) {
        exitState(currentState);
        currentState = state;
        enterState(state);
    }
    
    public State getState() {
        return currentState;
    }

    public void enterState(State state) {
        switch (state) {
            case CRAWL:
                enterCRAWL();
                break;
            case EDITMAP:
                enterEDITMAP();
                break;
            case MAP:
                enterMAP();
                break;
        }
    }

    public void exitState(State state) {
        switch (state) {
            case CRAWL:
                exitCRAWL();
                break;
            case EDITMAP:
                exitEDITMAP();
                break;
            case MAP:
                exitMAP();
                break;
        }
    }

    private void enterCRAWL() {
        engine.getSystem(AISystem.class).setProcessing(true);
        engine.getSystem(FPVRenderingSystem.class).setProcessing(true);
        contextManager.activateContext(ContextManager.ContextType.CRAWLING);
        mapLayer.setDirty(true);
    }

    private void exitCRAWL() {
        engine.getSystem(FPVRenderingSystem.class).setProcessing(false);
        contextManager.disableContext(ContextManager.ContextType.CRAWLING);
    }

    private void enterEDITMAP() {
        engine.getSystem(AISystem.class).setProcessing(false);
        engine.getSystem(MapRenderingSystem.class).setProcessing(true);
        contextManager.activateContext(ContextManager.ContextType.EDITMAP);
        mapLayer.setDirty(true);
    }

    private void exitEDITMAP() {
        engine.getSystem(MapRenderingSystem.class).setProcessing(false);
        contextManager.disableContext(ContextManager.ContextType.EDITMAP);
    }

    private void enterMAP() {
        engine.getSystem(AISystem.class).setProcessing(false);
        engine.getSystem(MapRenderingSystem.class).setProcessing(true);
        contextManager.activateContext(ContextManager.ContextType.MAP);
        mapLayer.setDirty(true);
    }

    private void exitMAP() {
        engine.getSystem(MapRenderingSystem.class).setProcessing(false);
        contextManager.disableContext(ContextManager.ContextType.MAP);
    }
}
