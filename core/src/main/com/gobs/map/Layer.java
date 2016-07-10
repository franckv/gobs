package com.gobs.map;

import com.gobs.map.LayerCell.LayerCellType;
import java.util.Iterator;

/**
 * Store a level
 */
public class Layer implements Iterable<LayerCell> {
    public enum LayerType {
        MAP_LAYER
    }

    private int w;
    private int h;
    private LayerType type;
    private boolean dirty;

    private LayerCell[][] cells;

    public Layer(int w, int h, LayerType type) {
        this.h = h;
        this.w = w;
        this.type = type;
        this.dirty = true;
        this.cells = new LayerCell[w][h];
    }

    public void setCell(int x, int y, LayerCellType cellType, boolean block) {
        if (x >= 0 && x < w && y >= 0 && y < h) {
            cells[x][y] = new LayerCell(x, y, cellType, block);
        }
        
        this.dirty = true;
    }

    public LayerCell getCell(int x, int y) {
        return cells[x][y];
    }

    public void reset() {
        this.cells = new LayerCell[w][h];
        this.dirty = true;
    }

    public int getWidth() {
        return w;
    }

    public int getHeight() {
        return h;
    }

    public LayerType getType() {
        return type;
    }
    
    public boolean isDirty() {
        return dirty;
    }
    
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public Iterator<LayerCell> iterator() {
        Iterator it = new Iterator() {
            private int idx = 0;

            @Override
            public Object next() {
                while (idx < w * h) {
                    int x = idx % w;
                    int y = idx / w;
                    idx += 1;
                    LayerCell cell = cells[x][y];
                    if (cell != null) {
                        return cell;
                    }
                }
                
                return null;
            }

            @Override
            public boolean hasNext() {
                return idx < w * h;
            }
        };

        return it;
    }
}
