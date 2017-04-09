package com.gobs.util;

import com.badlogic.gdx.utils.Array;
import com.gobs.pathfinding.Graph;

public class CollisionManager<T> {
    private Graph<T> graph;
    private QuadTree<T> entityTree;
    private QuadTree<T> blockableTree;

    public CollisionManager(int width, int height) {
        entityTree = new PointQuadTree<>(height - 1, 0, 0, width - 1);
        blockableTree = new PointQuadTree<>(height - 1, 0, 0, width - 1);

        graph = new Graph<>(this, width, height, false);
    }

    public Graph getGraph() {
        return graph;
    }

    public void resetEntities() {
        entityTree.reset();
    }

    public void resetBlockable() {
        blockableTree.reset();
    }

    public void addEntity(T e, int x, int y) {
        entityTree.insert(e, x, y);
    }

    public void addBlockable(int x, int y) {
        blockableTree.insert(null, x, y);
    }

    public boolean isColliding(T e, int x, int y) {
        Array<T> resultEntities = entityTree.find(x, y);
        Array<T> resultBlockables = blockableTree.find(x, y);

        return (resultEntities != null && (resultEntities.size > 1 || resultEntities.get(0) != e))
                || (resultBlockables != null && (resultBlockables.size > 1 || resultBlockables.get(0) != e));
    }

    public boolean isBlocked(int x, int y) {
        Array<T> resultEntities = entityTree.find(x, y);
        Array<T> resultBlockables = blockableTree.find(x, y);

        return (resultEntities != null && resultEntities.size > 0)
                || (resultBlockables != null && resultBlockables.size > 0);
    }
}
