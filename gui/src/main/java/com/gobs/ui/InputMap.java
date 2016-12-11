package com.gobs.ui;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Represent a set of inputs for an entity
 */
public class InputMap {
    BitSet inputs;
    int mouseX, mouseY;
    boolean mouseDown;

    public InputMap() {
        inputs = new BitSet(Input.values().length);
        mouseDown = false;
    }

    public InputMap(InputMap clone) {
        inputs = new BitSet(clone.inputs.length());
        inputs.or(clone.inputs);
        mouseDown = false;
    }

    public void set(Input input) {
        inputs.set(input.ordinal());
    }

    public void clear(Input input) {
        inputs.clear(input.ordinal());
    }

    public Set<Input> get() {
        Set<Input> set = new HashSet<>();
        
        for (int i = inputs.nextSetBit(0); i >= 0; i = inputs.nextSetBit(i + 1)) {
            set.add(Input.values()[i]);
        }
        
        return set;
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

    public void mouseMoved(int screenX, int screenY) {
        this.mouseX = screenX;
        this.mouseY = screenY;
    }

    public void setMouseDown(boolean down) {
        mouseDown = down;
    }
}
