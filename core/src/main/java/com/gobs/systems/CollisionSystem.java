package com.gobs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.gobs.components.Animation;
import com.gobs.components.Collider;
import com.gobs.components.Position;
import com.gobs.components.Transform;
import com.gobs.map.LevelCell;
import com.gobs.map.WorldMap;
import com.gobs.util.CollisionManager;

public class CollisionSystem extends IteratingSystem {
    private ComponentMapper<Transform> tm;
    private ComponentMapper<Position> pm;
    private ComponentMapper<Collider> cm;
    private ComponentMapper<Animation> am;

    @Wire
    private CollisionManager<Integer> collisionManager;
    @Wire
    private WorldMap worldMap;

    private EntitySubscription colliders;

    public CollisionSystem() {
        super(Aspect.all(Position.class, Transform.class));
    }

    @Override
    protected void initialize() {
        this.colliders = getWorld().getAspectSubscriptionManager().get(Aspect.all(Collider.class, Position.class));
    }

    @Override
    protected void begin() {
        collisionManager.reset();

        buildTree(colliders.getEntities());

        for (LevelCell cell : worldMap.getCurrentLevel()) {
            if (cell != null && cell.isBlockable()) {
                collisionManager.addBlockable(cell.getX(), cell.getY());
            }
        }
    }

    @Override
    protected void process(int entityId) {
        Position pos = pm.get(entityId);
        Transform trans = tm.get(entityId);

        int x = pos.getX();
        int dx = trans.getDX();
        int y = pos.getY();
        int dy = trans.getDY();

        // TODO: trigger scrolling if moving out of screen
        if (checkBounds(x + dx, y + dy, worldMap.getWorldWidth(), worldMap.getWorldHeight()) || checkColliders(entityId, x + dx, y + dy)) {
            trans.setDX(0);
            trans.setDY(0);
            // TODO: check if animation type is translation
            // TODO: add bouncing animation
            am.remove(entityId);
        }
    }

    private void buildTree(IntBag colliders) {
        for (int i = 0; i < colliders.size(); i++) {
            int entityId = colliders.get(i);

            Position pos = pm.get(entityId);
            Transform trans = tm.get(entityId);

            if (trans != null) {
                collisionManager.addEntity(entityId, pos.getX() + trans.getDX(), pos.getY() + trans.getDY());
            } else {
                collisionManager.addEntity(entityId, pos.getX(), pos.getY());
            }
        }
    }

    private boolean checkBounds(int x, int y, int width, int height) {
        return x < 0 || y < 0 || x > width - 1 || y > height - 1;
    }

    private boolean checkColliders(int entityId, int x, int y) {
        if (cm.get(entityId) != null) {
            return collisionManager.isColliding(entityId, x, y);
        }
        return false;
    }
}
