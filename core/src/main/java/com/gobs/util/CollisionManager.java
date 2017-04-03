package com.gobs.util;

import com.gobs.pathfinding.Graph;
import java.util.List;

/**
 *
 */
public class CollisionManager<T> {
    private Graph<T> graph;
    private QuadTree<T> tree;
    
    public CollisionManager(int width, int height) {
        tree = new PointQuadTree<>(height - 1, 0, 0, width - 1);

        graph = new Graph<>(this, width, height, false);
    }

    public Graph getGraph() {
        return graph;
    }

    public QuadTree<T> getTree() {
        return tree;
    }

    public void reset() {
        tree.reset();
    }

    public void addEntity(T e, int x, int y) {
        tree.insert(e, x, y);
    }

    public void addBlockable(int x, int y) {
        tree.insert(null, x, y);
    }
    
    public boolean isColliding(T e, int x, int y) {
        List<T> result = tree.find(x, y);

        return result != null && (result.size() > 1 || result.get(0) != e);
    }

    public boolean isBlocked(int x, int y) {
        List<T> result = tree.find(x, y);
        
        return result != null && !result.isEmpty();
    }
}
