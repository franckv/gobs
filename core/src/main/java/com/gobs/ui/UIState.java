package com.gobs.ui;

import com.gobs.ui.gdx.GdxGUILoader;

public class UIState {
    public enum State {
        NONE, CRAWL, INVENTORY
    }

    private State currentState;
    private GdxGUILoader guiLoader;

    public UIState(GdxGUILoader guiLoader) {
        currentState = State.CRAWL;
        this.guiLoader = guiLoader;
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
                guiLoader.enableFragment("characters", true);
                break;
            case NONE:
                guiLoader.enableFragment("characters", false);
                break;
            case INVENTORY:
                guiLoader.enableFragment("inventory", true);
                break;
        }
    }

    private void exitState(State state) {
        switch (state) {
            case CRAWL:
                break;
            case NONE:
                break;
            case INVENTORY:
                guiLoader.enableFragment("inventory", false);
                break;
        }
    }

}
