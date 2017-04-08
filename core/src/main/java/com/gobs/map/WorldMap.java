package com.gobs.map;

import com.badlogic.gdx.utils.Array;

public class WorldMap {
    Array<Level> levels;
    int currentLevel;
    int worldWidth;
    int worldHeight;

    public WorldMap(int worldWidth, int worldHeight) {
        levels = new Array<>();
        currentLevel = 0;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public void addLayer(Level layer) {
        levels.add(layer);
        if (currentLevel == 0) {
            currentLevel = 1;
        }
    }

    public int getCurrentLevelIndex() {
        return currentLevel;
    }

    public Level getCurrentLevel() {
        return levels.get(currentLevel - 1);
    }

    public void setCurrentLevel(int level) {
        currentLevel = Math.min(level, levels.size);
    }

    public int getWorldHeight() {
        return worldHeight;
    }

    public int getWorldWidth() {
        return worldWidth;
    }
}
