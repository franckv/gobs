package com.gobs.util;

import com.badlogic.gdx.math.GridPoint2;
import java.util.List;

public interface QuadTree<E> {
    public void insert(E e, int x, int y);

    public GridPoint2 getCoord();

    public void reset();

    public int getLevel();

    public boolean hasChildren();

    public List<E> find(int x, int y);

    public QuadTree<E> getNE();

    public QuadTree<E> getNW();

    public QuadTree<E> getSE();

    public QuadTree<E> getSW();

    public int getTop();

    public int getBottom();

    public int getLeft();

    public int getRight();
}
