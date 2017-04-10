package com.gobs.ui;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Represent a set of inputs for an entity
 */
public class InputMap {
    public enum Input {
        LEFT, RIGHT, UP, DOWN, SPACE, ENTER, DEL, ESCAPE, TAB,
        A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z
    }

    BitSet active;
    BitSet changed;
    int[] frames;
    int repeat;
    int rate;

    int mouseX, mouseY;
    boolean mouseDown;

    public InputMap() {
        this(0, 1);
    }

    public InputMap(int repeat, int rate) {
        int len = Input.values().length;
        active = new BitSet(len);
        changed = new BitSet(len);
        frames = new int[len];
        this.repeat = repeat;
        this.rate = rate;

        mouseDown = false;
    }

    public void set(Input input) {
        active.set(input.ordinal());
        changed.set(input.ordinal());
        frames[input.ordinal()] = 0;
    }

    public void clear(Input input) {
        active.clear(input.ordinal());
        changed.set(input.ordinal());
        frames[input.ordinal()] = 0;
    }

    public void done() {
        changed.clear();
        for (int i = active.nextSetBit(0); i >= 0; i = active.nextSetBit(i + 1)) {
            frames[i]++;
            if (frames[i] > repeat) {
                frames[i] += rate - 1;
            }
        }
    }

    public Set<Input> getActive() {
        return get(active);
    }

    public Set<Input> getPressed() {
        Set<Input> set = new HashSet<>();

        for (int i = active.nextSetBit(0); i >= 0; i = active.nextSetBit(i + 1)) {
            if (changed.get(i)) {
                set.add(Input.values()[i]);
            } else if (repeat > 0 && frames[i] % repeat == 0) {
                set.add(Input.values()[i]);
            }
        }

        return set;
    }

    private Set<Input> get(BitSet input) {
        Set<Input> set = new HashSet<>();

        for (int i = input.nextSetBit(0); i >= 0; i = input.nextSetBit(i + 1)) {
            set.add(Input.values()[i]);
        }

        return set;
    }

    public boolean hasInput() {
        return !active.isEmpty() || mouseDown;
    }

    public boolean isActive(Input input) {
        return active.get(input.ordinal());
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
