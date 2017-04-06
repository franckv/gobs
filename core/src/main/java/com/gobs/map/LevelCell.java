package com.gobs.map;

/**
 * A cell in a level
 */
public class LevelCell {
    private int x, y;
    private LevelCellType type;

    public enum LevelCellType {
        FLOOR, WALL, STAIRS
    }

    public LevelCell(int x, int y, LevelCellType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public boolean isBlockable() {
        boolean blockable = false;

        switch (type) {
            case FLOOR:
                blockable = false;
                break;
            case WALL:
                blockable = true;
                break;
            case STAIRS:
                blockable = false;
                break;
        }

        return blockable;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public LevelCellType getType() {
        return type;
    }
}
