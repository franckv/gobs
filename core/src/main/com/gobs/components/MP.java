package com.gobs.components;

import com.badlogic.ashley.core.Component;

/**
 *
 */
public class MP implements Component {
    private int mp;
    private int maxMP;

    public MP(int mp, int maxMP) {
        this.mp = mp;
        this.maxMP = maxMP;
    }

    public int getMP() {
        return mp;
    }

    public int getMaxMP() {
        return maxMP;
    }

    public void addMP(int d) {
        mp += d;
        if (mp < 0) {
            mp = 0;
        } else if (mp > maxMP) {
            mp = maxMP;
        }
    }
    
    public void addMaxMP(int d) {
        maxMP += d;
    }
    
    public void setToMax() {
        mp = maxMP;
    }
}
