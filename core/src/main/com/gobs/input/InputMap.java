package com.gobs.input;

import com.badlogic.gdx.utils.Bits;

/**
 * Represent a set of inputs for an entity
 */
public class InputMap {
    Bits inputs;
    int mouseX, mouseY;
    boolean mouseDown;

    public InputMap() {
        inputs = new Bits(Input.values().length);
        mouseDown = false;
    }

    public InputMap(InputMap clone) {
        inputs = new Bits(clone.inputs.length());
        inputs.or(clone.inputs);
        mouseDown = false;
    }

    public void set(Input input) {
        inputs.set(input.ordinal());
    }

    public void clear(Input input) {
        inputs.clear(input.ordinal());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        return this.inputs.equals(((InputMap) other).inputs);
    }

    public boolean hasInput() {
        return !inputs.isEmpty() || mouseDown;
    }

    public boolean isPressed(Input input) {
        return inputs.get(input.ordinal());
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public boolean isMouseDown() {
        return mouseDown;
    }

    void mouseMoved(int screenX, int screenY) {
        this.mouseX = screenX;
        this.mouseY = screenY;
    }

    void setMouseDown(boolean down) {
        mouseDown = down;
    }
}
