package com.gobs.components;

import com.badlogic.ashley.core.Component;

/**
 * Party member
 */
public class Party implements Component {
    int pos;

    public Party(int pos) {
        this.pos = pos;
    }

    public int getPos() {
        return pos;
    }
}
