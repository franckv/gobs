package com.gobs.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import java.util.ArrayList;
import java.util.List;

public class PointQuadTree<E> implements QuadTree<E> {
    int level;
    List<E> elements;
    GridPoint2 coord;

    int top, bottom, left, right;
    PointQuadTree<E> ne, nw, se, sw;

    public PointQuadTree(int top, int bottom, int left, int right) {
        this(top, bottom, left, right, 0);
    }

    private PointQuadTree(int top, int bottom, int left, int right, int level) {
        this.level = level;
        this.elements = null;
        this.coord = null;
        this.ne = this.nw = this.se = this.sw = null;
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;

        if (level > 100) {
            throw new RuntimeException("Too many level: " + level);
        }
    }

    @Override
    public void insert(E e, int x, int y) {
        Gdx.app.debug("PointQuadTree", "insert node " + e + " " + x + ":" + y);
        if (elements == null) {
            elements = new ArrayList<>();
            elements.add(e);
            coord = new GridPoint2(x, y);
            Gdx.app.debug("PointQuadTree", "inserted level " + level);
        } else if (coord.x == x && coord.y == y) {
            //Gdx.app.debug("PointQuadTree", "duplicate node");
            elements.add(e);
        } else {
            int idx = getIndex(x, y);
            Gdx.app.debug("PointQuadTree", "insert child " + idx);
            getChild(idx).insert(e, x, y);
        }
    }

    @Override
    public void reset() {
        this.elements = null;
        this.coord = null;
        this.ne = this.nw = this.se = this.sw = null;
    }

    @Override
    public GridPoint2 getCoord() {
        return coord;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public boolean hasChildren() {
        return ne != null;
    }

    @Override
    public List<E> find(int x, int y) {
        List<E> result;

        if (coord == null) {
            return null;
        }

        if (coord.x == x && coord.y == y) {
            result = elements;
        } else if (ne == null) {
            result = null;
        } else {
            if ((result = ne.find(x, y)) != null) {

            } else if ((result = nw.find(x, y)) != null) {

            } else if ((result = se.find(x, y)) != null) {

            } else if ((result = sw.find(x, y)) != null) {

            }
        }

        return result;
    }

    @Override
    public QuadTree<E> getNE() {
        return this.ne;
    }

    @Override
    public QuadTree<E> getNW() {
        return this.nw;
    }

    @Override
    public QuadTree<E> getSE() {
        return this.se;
    }

    @Override
    public QuadTree<E> getSW() {
        return this.sw;
    }

    @Override
    public int getTop() {
        return top;
    }

    @Override
    public int getBottom() {
        return bottom;
    }

    @Override
    public int getLeft() {
        return left;
    }

    @Override
    public int getRight() {
        return right;
    }

    private PointQuadTree<E> getChild(int idx) {
        /*
        ***********************
        *          <          *
        *   NW(2)  <   NE(3)  *
        *          <          *
        *vvvvvvvvvv*vvvvvvvvvv*
        *          <          *
        *   SW(0)  <   SE(1)  *
        *          <          *
        ***********************
         */
        if (ne == null) {
            ne = new PointQuadTree<>(top, coord.y+1, coord.x+1, right, level + 1);
            nw = new PointQuadTree<>(top, coord.y+1, left, coord.x, level + 1);
            se = new PointQuadTree<>(coord.y, bottom, coord.x+1, right, level + 1);
            sw = new PointQuadTree<>(coord.y, bottom, left, coord.x, level + 1);
        }

        switch (idx) {
            case 0:
                return sw;
            case 1:
                return se;
            case 2:
                return nw;
            default:
                return ne;
        }
    }

    private int getIndex(int x, int y) {
        int idx;
        if (x <= coord.x && y <= coord.y) {
            // SW
            idx = 0;
        } else if (x > coord.x && y <= coord.y) {
            // SE
            idx = 1;
        } else if (x <= coord.x && y > coord.y) {
            // NW 
            idx = 2;
        } else {
            // NE
            idx = 3;
        }

        return idx;
    }
}
