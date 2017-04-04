package com.gobs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.gobs.GobsEngine;
import com.gobs.components.Position;
import com.gobs.components.Transform;

public class TransformationSystem extends IteratingSystem {
    private static ComponentMapper<Transform> tm = ComponentMapper.getFor(Transform.class);
    private static ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);

    public TransformationSystem() {
        this(0);
    }

    public TransformationSystem(int priority) {
        super(Family.all(Position.class, Transform.class).get(), priority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Position position = pm.get(entity);
        Transform trans = tm.get(entity);

        position.translate(trans.getDX(), trans.getDY());

        entity.remove(Transform.class);
    }

    @Override
    public boolean checkProcessing() {
        return !((GobsEngine) getEngine()).isRendering() && super.checkProcessing();
    }
}
