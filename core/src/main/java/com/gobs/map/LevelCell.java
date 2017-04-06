package com.gobs.map;

/**
 * A cell in a level
 */
public class LevelCell {
    private boolean isBlockable;
    private int x, y;
    private LevelCellType type;
    
    public enum LevelCellType {
        FLOOR, WALL, STAIRS
    }
    
    public LevelCell(int x, int y, LevelCellType type, boolean block) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.isBlockable = block;
    }
    
    public boolean isBlockable() {
        return isBlockable;
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
