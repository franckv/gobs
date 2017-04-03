package com.gobs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.gobs.map.Layer;
import com.gobs.map.LayerCell;
import com.gobs.components.Collider;
import com.gobs.components.Position;
import com.gobs.components.Transform;
import com.gobs.util.CollisionManager;

public class CollisionSystem extends EntityProcessingSystem {
    private CollisionManager<Entity> collisionManager;
    private int worldWidth;
    private int worldHeight;
    private Layer mapLayer;

    private ImmutableArray<Entity> colliders;

    private ComponentMapper<Transform> tm = ComponentMapper.getFor(Transform.class);
    private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
    private ComponentMapper<Collider> cm = ComponentMapper.getFor(Collider.class);

    public CollisionSystem(CollisionManager<Entity> collisionManager, int worldWidth, int worldHeight, Layer mapLayer) {
        this(collisionManager, worldWidth, worldHeight, mapLayer, 0);
    }

    public CollisionSystem(CollisionManager<Entity> collisionManager, int worldWidth, int worldHeight, Layer mapLayer, int priority) {
        super(Family.all(Position.class, Transform.class).get(), priority);

        this.collisionManager = collisionManager;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.mapLayer = mapLayer;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        colliders = engine.getEntitiesFor(Family.all(Collider.class).get());
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        colliders = null;
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : getEntities()) {
            Position pos = pm.get(entity);
            Transform trans = tm.get(entity);

            int x = pos.getX();
            int dx = trans.getDX();
            int y = pos.getY();
            int dy = trans.getDY();

            Gdx.app.debug("CollisionSystem", x + dx + ":" + y + dy);

            // TODO: trigger scrolling if moving out of screen
            if (checkBounds(x + dx, y + dy, worldWidth, worldHeight)) {
                trans.setDX(0);
                trans.setDY(0);
            }
        }

        if (getEntities().size() > 0) {
            collisionManager.reset();
            buildTree();
        }

        for (LayerCell cell : mapLayer) {
            if (cell != null && cell.isBlockable()) {
                collisionManager.addBlockable(cell.getX(), cell.getY());
            }
        }

        for (Entity entity : getEntities()) {
            Position pos = pm.get(entity);
            Transform trans = tm.get(entity);

            int x = pos.getX();
            int dx = trans.getDX();
            int y = pos.getY();
            int dy = trans.getDY();

            if (checkColliders(entity, x + dx, y + dy)) {
                trans.setDX(0);
                trans.setDY(0);
            }
        }
    }

    private void buildTree() {
        for (Entity entity : colliders) {
            Position pos = pm.get(entity);
            Transform trans = tm.get(entity);

            if (trans != null) {
                collisionManager.addEntity(entity, pos.getX() + trans.getDX(), pos.getY() + trans.getDY());
            } else {
                collisionManager.addEntity(entity, pos.getX(), pos.getY());
            }
        }
    }

    private boolean checkBounds(int x, int y, int width, int height) {
        return x < 0 || y < 0 || x > width - 1 || y > height - 1;
    }

    private boolean checkColliders(Entity entity, int x, int y) {
        if (cm.get(entity) != null) {
            return collisionManager.isColliding(entity, x, y);
        }
        return false;
    }

    @Override
    public void dispose() {
    }
}
