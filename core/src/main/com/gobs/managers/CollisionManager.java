package com.gobs.managers;

import com.badlogic.ashley.core.Entity;
import com.gobs.pathfinding.Graph;
import com.gobs.util.PointQuadTree;
import com.gobs.util.QuadTree;
import java.util.List;

/**
 *
 */
public class CollisionManager {
    private Graph graph;
    private QuadTree<Entity> tree;
    
    public CollisionManager(int width, int height) {
        tree = new PointQuadTree<>(height - 1, 0, 0, width - 1);

        graph = new Graph(this, width, height);
    }

    public Graph getGraph() {
        return graph;
    }

    public QuadTree<Entity> getTree() {
        return tree;
    }

    public void reset() {
        tree.reset();
    }

    public void addEntity(Entity e, int x, int y) {
        tree.insert(e, x, y);
    }

    public void addBlockable(int x, int y) {
        tree.insert(null, x, y);
    }
    
    public boolean isColliding(Entity e, int x, int y) {
        List<Entity> result = tree.find(x, y);

        return result != null && (result.size() > 1 || result.get(0) != e);
    }

    public boolean isBlocked(int x, int y) {
        List<Entity> result = tree.find(x, y);
        
        return result != null && !result.isEmpty();
    }
}
