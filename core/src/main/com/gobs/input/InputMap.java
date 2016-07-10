package com.gobs.input;

import com.badlogic.gdx.utils.Bits;

/**
 * Represent a set of inputs for an entity
 */
public class InputMap {
    Bits inputs;

    public InputMap() {
        inputs = new Bits(Input.values().length);
    }

    public InputMap(InputMap clone) {
        inputs = new Bits(clone.inputs.length());
        inputs.or(clone.inputs);
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
        return !inputs.isEmpty();
    }

    public boolean isPressed(Input input) {
        return inputs.get(input.ordinal());
    }

    public void reset() {
        inputs.clear();
    }
}
