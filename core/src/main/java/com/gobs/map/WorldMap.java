package com.gobs.map;

import java.util.ArrayList;
import java.util.List;

public class WorldMap {
    List<Layer> levels;
    int currentLevel;
    int worldWidth;
    int worldHeight;

    public WorldMap(int worldWidth, int worldHeight) {
        levels = new ArrayList<>();
        currentLevel = 0;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public void addLayer(Layer layer) {
        levels.add(layer);
        if (currentLevel == 0) {
            currentLevel = 1;
        }
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public Layer getCurrentLayer() {
        return levels.get(currentLevel - 1);
    }

    public void setCurrentLevel(int level) {
        currentLevel = Math.min(level, levels.size());
    }

    public int getWorldHeight() {
        return worldHeight;
    }

    public int getWorldWidth() {
        return worldWidth;
    }
}
