package com.gobs.components;

import com.badlogic.ashley.core.Component;

/**
 *
 */
public class HP implements Component {
    private int hp;
    private int maxHP;

    public HP(int hp, int maxHP) {
        this.hp = hp;
        this.maxHP = maxHP;
    }

    public int getHP() {
        return hp;
    }

    public int getMaxHP() {
        return maxHP;
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
