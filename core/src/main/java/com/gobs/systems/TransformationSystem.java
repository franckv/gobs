package com.gobs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.gobs.GobsEngine;
import com.gobs.components.Animation;
import com.gobs.components.Camera;
import com.gobs.components.Position;
import com.gobs.components.Transform;

public class TransformationSystem extends IteratingSystem {
    private static ComponentMapper<Transform> tm = ComponentMapper.getFor(Transform.class);
    private static ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
    private static ComponentMapper<Camera> cm = ComponentMapper.getFor(Camera.class);

    public TransformationSystem() {
        this(0);
    }

    public TransformationSystem(int priority) {
        super(Family.all(Position.class, Transform.class).exclude(Animation.class).get(), priority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Position position = pm.get(entity);
        Transform trans = tm.get(entity);
        Camera cam = cm.get(entity);

        position.translate(trans.getDX(), trans.getDY());

        if (cam != null) {
            cam.rotate(trans.getRotation());
        }

        entity.remove(Transform.class);
    }

    @Override
    public boolean checkProcessing() {
        return !((GobsEngine) getEngine()).isRendering() && super.checkProcessing();
    }
}
