package com.gobs;

import com.gobs.input.ContextManager;

public class StateManager {
    public enum State {
        CRAWL,
        EDITMAP,
        MAP
    }

    private State currentState;
    private ContextManager contextManager;

    public StateManager(ContextManager contextManager, State state) {
        this.contextManager = contextManager;
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

    private void enterState(State state) {
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

    private void exitState(State state) {
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
        contextManager.activateContext(ContextManager.ContextType.CRAWLING);
    }

    private void exitCRAWL() {
        contextManager.disableContext(ContextManager.ContextType.CRAWLING);
    }

    private void enterEDITMAP() {
        contextManager.activateContext(ContextManager.ContextType.EDITMAP);
    }

    private void exitEDITMAP() {
        contextManager.disableContext(ContextManager.ContextType.EDITMAP);
    }

    private void enterMAP() {
        contextManager.activateContext(ContextManager.ContextType.MAP);
    }

    private void exitMAP() {
        contextManager.disableContext(ContextManager.ContextType.MAP);
    }
}
