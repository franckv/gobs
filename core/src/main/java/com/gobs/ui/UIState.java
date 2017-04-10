package com.gobs.ui;

public class UIState {
    public enum State {
        NONE, CRAWL, INVENTORY
    }

    private State currentState;
    private GUI gui;

    public UIState(GUI gui) {
        currentState = State.CRAWL;
        this.gui = gui;
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
                gui.enableFragment("characters", true);
                break;
            case NONE:
                gui.enableFragment("characters", false);
                break;
            case INVENTORY:
                gui.enableFragment("inventory", true);
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
                gui.enableFragment("inventory", false);
                break;
        }
    }

}
