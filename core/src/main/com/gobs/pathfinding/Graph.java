package com.gobs.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.gobs.managers.CollisionManager;
import java.util.HashMap;
import java.util.Map;

public class Graph implements IndexedGraph<GridPoint2> {
    private int width;
    private int height;
    private boolean diags;
    private CollisionManager collisionManager;
    Map<String, GridPoint2> nodes;

    public Graph(CollisionManager collisionManager, int width, int height) {
        this.collisionManager = collisionManager;
        this.width = width;
        this.height = height;

        // entity can move only in 4 cardinal directions
        this.diags = false;

        this.nodes = new HashMap<>();
    }

    @Override
    public int getIndex(GridPoint2 node) {
        return node.x * height + node.y;
    }

    @Override
    public int getNodeCount() {
        return width * height;
    }

    public GridPoint2 getNode(int x, int y) {
        String k = x + ":" + y;

        if (!nodes.containsKey(k)) {
            nodes.put(k, new GridPoint2(x, y));
        }

        return nodes.get(k);
    }

    @Override
    public Array<Connection<GridPoint2>> getConnections(GridPoint2 fromNode) {
        Array<Connection<GridPoint2>> list = new Array<>();

        // W
        addConnection(fromNode, fromNode.x - 1, fromNode.y, list);
        // S
        addConnection(fromNode, fromNode.x, fromNode.y - 1, list);
        // E
        addConnection(fromNode, fromNode.x + 1, fromNode.y, list);
        // N
        addConnection(fromNode, fromNode.x, fromNode.y + 1, list);

        if (diags) {
            // SW
            addConnection(fromNode, fromNode.x - 1, fromNode.y - 1, list);
            // NW
            addConnection(fromNode, fromNode.x - 1, fromNode.y + 1, list);
            // SE
            addConnection(fromNode, fromNode.x + 1, fromNode.y - 1, list);
            // NE
            addConnection(fromNode, fromNode.x + 1, fromNode.y + 1, list);
        }

        return list;
    }

    private void addConnection(GridPoint2 fromNode, int x, int y, Array<Connection<GridPoint2>> list) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return;
        }

        if (!collisionManager.isBlocked(x, y)) {
            list.add(new DefaultConnection<>(fromNode, getNode(x, y)));
        }
    }
}
