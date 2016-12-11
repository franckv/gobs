package com.gobs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.gobs.components.Camera;
import com.gobs.components.Position;

/**
 *
 */
public class CameraSystem extends EntityProcessingSystem {
    private ComponentMapper<Camera> cm = ComponentMapper.getFor(Camera.class);
    private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);

    public CameraSystem() {
        this(0);
    }

    public CameraSystem(int priority) {
        super(Family.all(Camera.class, Position.class).get(), priority);
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : getEntities()) {
            Camera cam = cm.get(entity);
            Position pos = pm.get(entity);
        }
    }

    @Override
    public void dispose() {
    }
}
