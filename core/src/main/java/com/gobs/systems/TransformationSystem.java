package com.gobs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.gobs.components.Position;
import com.gobs.components.Transform;

public class TransformationSystem extends EntityProcessingSystem {
    private static ComponentMapper<Transform> tm = ComponentMapper.getFor(Transform.class);
    private static ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);

    public TransformationSystem() {
        this(0);
    }

    public TransformationSystem(int priority) {
        super(Family.all(Position.class, Transform.class).get(), priority);
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : getEntities()) {
            Position position = pm.get(entity);
            Transform trans = tm.get(entity);

            position.translate(trans.getDX(), trans.getDY());

            entity.remove(Transform.class);
        }
    }

    @Override
    public void dispose() {
    }
}
