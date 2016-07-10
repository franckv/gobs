package com.gobs.map;

/**
 * A cell in a level
 */
public class LayerCell {
    private boolean isBlockable;
    private int x, y;
    private LayerCellType type;
    
    public enum LayerCellType {
        FLOOR, WALL, STAIRS
    }
    
    public LayerCell(int x, int y, LayerCellType type, boolean block) {
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
    
    public LayerCellType getType() {
        return type;
    }
}
