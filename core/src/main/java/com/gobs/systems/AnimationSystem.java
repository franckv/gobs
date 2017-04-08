package com.gobs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.gobs.components.Animation;
import com.gobs.components.Camera;
import com.gobs.components.Position;
import com.gobs.components.Progress;
import com.gobs.components.Transform;

public class AnimationSystem extends IteratingSystem {
    private static ComponentMapper<Animation> am;
    private static ComponentMapper<Position> pm;
    private static ComponentMapper<Transform> tm;
    private static ComponentMapper<Camera> cm;
    private static ComponentMapper<Progress> sm;

    public AnimationSystem() {
        super(Aspect.all(Animation.class, Position.class, Transform.class, Progress.class));
    }

    @Override
    public boolean checkProcessing() {
        return super.checkProcessing();
        //return !((GobsEngine) getEngine()).isRendering() && super.checkProcessing();
    }

    @Override
    protected void process(int entityId) {
        Animation anim = am.get(entityId);
        Progress progress = sm.get(entityId);
        Position pos = pm.get(entityId);
        Transform trans = tm.get(entityId);
        Camera cam = cm.get(entityId);

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
            am.remove(entityId);
            sm.remove(entityId);
        }
    }
}
