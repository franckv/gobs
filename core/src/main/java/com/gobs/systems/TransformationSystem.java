package com.gobs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.gobs.components.Animation;
import com.gobs.components.Camera;
import com.gobs.components.Position;
import com.gobs.components.Transform;

public class TransformationSystem extends IteratingSystem {
    private static ComponentMapper<Transform> tm;
    private static ComponentMapper<Position> pm;
    private static ComponentMapper<Camera> cm;

    public TransformationSystem() {
        super(Aspect.all(Position.class, Transform.class).exclude(Animation.class));
    }

    @Override
    protected void process(int entityId) {
        Position position = pm.get(entityId);
        Transform trans = tm.get(entityId);
        Camera cam = cm.get(entityId);

        position.translate(trans.getDX(), trans.getDY());

        if (cam != null) {
            cam.rotate(trans.getRotation());
        }

        tm.remove(entityId);
    }
}
