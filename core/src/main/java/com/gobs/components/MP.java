package com.gobs.components;

import com.artemis.Component;

public class MP extends Component {
    private int mp;
    private int maxMP;

    public int getMP() {
        return mp;
    }

    public void setMP(int mp) {
        this.mp = mp;
    }

    public int getMaxMP() {
        return maxMP;
    }

    public void setMaxMP(int maxMP) {
        this.maxMP = maxMP;
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
