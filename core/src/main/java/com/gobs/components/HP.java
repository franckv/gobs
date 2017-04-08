package com.gobs.components;

import com.artemis.Component;

public class HP extends Component {
    private int hp;
    private int maxHP;

    public int getHP() {
        return hp;
    }

    public void setHP(int hp) {
        this.hp = hp;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public void addHP(int d) {
        hp += d;
        if (hp < 0) {
            hp = 0;
        } else if (hp > maxHP) {
            hp = maxHP;
        }
    }

    public void addMaxHP(int d) {
        maxHP += d;
    }

    public void setToMax() {
        hp = maxHP;
    }
}
