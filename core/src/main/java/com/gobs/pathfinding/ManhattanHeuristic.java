package com.gobs.pathfinding;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.GridPoint2;

public class ManhattanHeuristic implements Heuristic<GridPoint2> {
    @Override
    public float estimate(GridPoint2 node, GridPoint2 endNode) {
        return Math.abs(endNode.x - node.x) + Math.abs(endNode.y - node.y);
    }
}
