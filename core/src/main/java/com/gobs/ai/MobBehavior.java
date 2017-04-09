package com.gobs.ai;

import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.GridPoint2;
import com.gobs.components.Command;
import com.gobs.components.Command.CommandType;
import com.gobs.components.Goal;
import com.gobs.components.Position;
import com.gobs.pathfinding.Graph;
import com.gobs.pathfinding.ManhattanHeuristic;
import com.gobs.util.CollisionManager;
import java.util.Random;

/**
 * AI behavior for basic mob
 */
public class MobBehavior implements AIBehavior {
    private static ComponentMapper<Position> pm;
    private static ComponentMapper<Command> cm;
    private static ComponentMapper<Goal> gm;

    private int entityId;
    @Wire
    private CollisionManager<Integer> collisionManager;

    private static Random rnd = new Random();

    StateMachine<MobBehavior, MobState> stateMachine;

    public MobBehavior(int entityId) {
        this.stateMachine = new DefaultStateMachine<>(this, MobState.WAITING);

        this.entityId = entityId;
    }

    @Override
    public void update() {
        stateMachine.update();
    }

    void isWaiting() {
        Goal goal = gm.get(entityId);

        if (goal == null) {
            int p = rnd.nextInt(10);
            if (p == 0) {
                doMove();
                stateMachine.changeState(MobState.MOVING);
            }
        } else {
            stateMachine.changeState(MobState.CHASING);
            System.out.println("Chasing");
        }
    }

    void isMoving() {
        int p = rnd.nextInt(10);

        if (p < 5) {
            doMove();
        } else {
            stateMachine.changeState(MobState.WAITING);
        }
    }

    void isChasing() {
        doChase();
    }

    private void doChase() {
        Goal goal = gm.get(entityId);
        Position pos = pm.get(entityId);

        boolean giveup = true;

        if (goal != null && pos != null) {
            if (pos.getX() == goal.getX() && pos.getY() == goal.getY()) {
                System.out.println("Target reached");
            } else if (!collisionManager.isBlocked(goal.getX(), goal.getY())) {
                GridPoint2 nextPos = getMove(pos.getX(), pos.getY(), goal.getX(), goal.getY());

                if (nextPos != null) {
                    System.out.println("Next move: " + pos.getX() + ":" + pos.getY() + " -> " + goal.getX() + ":" + goal.getY() + " (" + nextPos.x + ":" + nextPos.y + ")");

                    giveup = false;

                    CommandType commandType = null;

                    if (nextPos.x - pos.getX() > 0) {
                        commandType = CommandType.RIGHT;
                    } else if (nextPos.x - pos.getX() < 0) {
                        commandType = CommandType.LEFT;
                    } else if (nextPos.y - pos.getY() > 0) {
                        commandType = CommandType.UP;
                    } else if (nextPos.y - pos.getY() < 0) {
                        commandType = CommandType.DOWN;
                    }

                    if (commandType != null) {
                        Command cmd = cm.create(entityId);
                        cmd.setCommand(commandType);
                    }
                }
            }
        }

        if (giveup) {
            gm.remove(entityId);
            System.out.println("Give up chase");
            stateMachine.changeState(MobState.WAITING);
        }
    }

    private void doMove() {
        int i = rnd.nextInt(4);

        CommandType commandType = null;

        switch (i) {
            case 0:
                commandType = CommandType.UP;
                break;

            case 1:
                commandType = CommandType.DOWN;
                break;
            case 2:
                commandType = CommandType.LEFT;
                break;
            case 3:
                commandType = CommandType.RIGHT;
                break;
        }

        if (commandType != null) {
            Command cmd = cm.create(entityId);
            cmd.setCommand(commandType);
        }
    }

    private GridPoint2 getMove(int x, int y, int a, int b) {
        Graph graph = collisionManager.getGraph();

        IndexedAStarPathFinder<GridPoint2> pf = new IndexedAStarPathFinder<>(graph, true);

        GraphPath<Connection<GridPoint2>> path = new DefaultGraphPath<>();

        boolean found = pf.searchConnectionPath(graph.getNode(x, y), graph.getNode(a, b), new ManhattanHeuristic(), path);

        if (found) {
            return path.get(0).getToNode();
        } else {
            return null;
        }
    }
}
