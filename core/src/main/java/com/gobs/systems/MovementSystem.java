package com.gobs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.gobs.components.Animation;
import com.gobs.components.Camera;
import com.gobs.components.Command;
import com.gobs.components.Transform;

public class MovementSystem extends IteratingSystem {
    private ComponentMapper<Command> cm;
    private ComponentMapper<Animation> am;
    private ComponentMapper<Transform> tm;
    private ComponentMapper<Camera> km;

    private int translateFrames;
    private int rotateFrames;

    public MovementSystem(int fps) {
        super(Aspect.all(Command.class));
        translateFrames = fps / 3;
        rotateFrames = fps / 3;
    }

    @Override
    protected void process(int entityId) {
        Command command = cm.get(entityId);
        Camera cam = km.get(entityId);

        Transform trans = tm.create(entityId);
        Animation anim = am.create(entityId);

        switch (command.getCommand()) {
            case LEFT:
                if (cam != null) {
                    trans.rotate(-90);
                    anim.setType(Animation.AnimationType.ROTATE);
                    anim.setDuration(rotateFrames);
                } else {
                    trans.addX(-1);
                    anim.setType(Animation.AnimationType.TRANSLATE);
                    anim.setDuration(translateFrames);
                }
                break;
            case RIGHT:
                if (cam != null) {
                    trans.rotate(90);
                    anim.setType(Animation.AnimationType.ROTATE);
                    anim.setDuration(rotateFrames);
                } else {
                    trans.addX(1);
                    anim.setType(Animation.AnimationType.TRANSLATE);
                    anim.setDuration(translateFrames);
                }
                break;
            case UP:
                if (cam != null) {
                    switch (cam.getOrientation()) {
                        case UP:
                            trans.addY(1);
                            break;
                        case DOWN:
                            trans.addY(-1);
                            break;
                        case RIGHT:
                            trans.addX(1);
                            break;
                        case LEFT:
                            trans.addX(-1);
                            break;
                    }
                } else {
                    trans.addY(1);
                }
                anim.setType(Animation.AnimationType.TRANSLATE);
                anim.setDuration(translateFrames);
                break;
            case DOWN:
                if (cam != null) {
                    switch (cam.getOrientation()) {
                        case UP:
                            trans.addY(-1);
                            break;
                        case DOWN:
                            trans.addY(1);
                            break;
                        case RIGHT:
                            trans.addX(-1);
                            break;
                        case LEFT:
                            trans.addX(1);
                            break;
                    }
                } else {
                    trans.addY(-1);
                }
                anim.setType(Animation.AnimationType.TRANSLATE);
                anim.setDuration(translateFrames);
                break;
        }

        cm.remove(entityId);
    }
}
