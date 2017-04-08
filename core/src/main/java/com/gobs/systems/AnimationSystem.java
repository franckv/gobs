package com.gobs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.gobs.GobsEngine;
import com.gobs.components.Animation;
import com.gobs.components.Camera;
import com.gobs.components.Position;
import com.gobs.components.Progress;
import com.gobs.components.Transform;

public class AnimationSystem extends IteratingSystem {
    private static ComponentMapper<Animation> am = ComponentMapper.getFor(Animation.class);
    private static ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
    private static ComponentMapper<Transform> tm = ComponentMapper.getFor(Transform.class);
    private static ComponentMapper<Camera> cm = ComponentMapper.getFor(Camera.class);
    private static ComponentMapper<Progress> sm = ComponentMapper.getFor(Progress.class);

    public AnimationSystem() {
        this(0);
    }

    public AnimationSystem(int priority) {
        super(Family.all(Animation.class, Position.class, Transform.class, Progress.class).get(), priority);
    }

    @Override
    public boolean checkProcessing() {
        return !((GobsEngine) getEngine()).isRendering() && super.checkProcessing();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Animation anim = am.get(entity);
        Progress progress = sm.get(entity);
        Position pos = pm.get(entity);
        Transform trans = tm.get(entity);
        Camera cam = cm.get(entity);

        progress.advance();
        if (!progress.isComplete()) {
            switch (anim.getType()) {
                case ROTATE:
                    if (cam != null) {
                        cam.setAngle((int) (progress.getCompletion() * trans.getRotation()));
                    }
                    break;
                case TRANSLATE:
                    pos.setDX(progress.getCompletion() * trans.getDX());
                    pos.setDY(progress.getCompletion() * trans.getDY());
                    break;
            }
        } else {
            entity.remove(Animation.class);
            entity.remove(Progress.class);
        }
    }
}
