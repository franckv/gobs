package com.gobs.map;

import com.gobs.map.LevelCell.LevelCellType;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Store a level
 */
public class Level implements Iterable<LevelCell> {
    private int w;
    private int h;
    private boolean dirty;

    private LevelCell[][] cells;

    public Level(int w, int h) {
        this.h = h;
        this.w = w;
        this.dirty = true;
        this.cells = new LevelCell[w][h];
    }

    public void setCell(int x, int y, LevelCellType cellType, boolean block) {
        if (x >= 0 && x < w && y >= 0 && y < h) {
            cells[x][y] = new LevelCell(x, y, cellType, block);
        }

        this.dirty = true;
    }

    public LevelCell getCell(int x, int y) {
        return cells[x][y];
    }

    public void reset() {
        this.cells = new LevelCell[w][h];
        this.dirty = true;
    }

    public int getWidth() {
        return w;
    }

    public int getHeight() {
        return h;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public Iterator<LevelCell> iterator() {
        Iterator it = new Iterator() {
            private int idx = 0;

            @Override
            public Object next() {
                int size = w * h;
                if (idx >= size) {
                    throw new NoSuchElementException();
                }
                int x = idx % w;
                int y = idx / w;
                idx += 1;
                return cells[x][y];
            }

            @Override
            public boolean hasNext() {
                int size = w * h;
                if (idx >= size) {
                    return false;
                }
                while (idx < size) {
                    int x = idx % w;
                    int y = idx / w;

                    LevelCell cell = cells[x][y];
                    if (cell != null) {
                        return true;
                    }
                    idx += 1;
                }

                return false;
            }
        };

        return it;
    }
}
