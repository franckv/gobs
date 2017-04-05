package com.gobs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.gobs.GobsEngine;
import com.gobs.components.Animation;
import com.gobs.components.Camera;
import com.gobs.components.Command;
import com.gobs.components.Transform;

public class MovementSystem extends IteratingSystem {
    private ComponentMapper<Command> cm = ComponentMapper.getFor(Command.class);
    private ComponentMapper<Camera> km = ComponentMapper.getFor(Camera.class);

    private int translateFrames;
    private int rotateFrames;

    public MovementSystem(int fps) {
        this(fps, 0);
    }

    public MovementSystem(int fps, int priority) {
        super(Family.all(Command.class).get(), priority);
        translateFrames = fps / 3;
        rotateFrames = fps / 3;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Command command = cm.get(entity);
        Camera cam = km.get(entity);

        Transform trans = new Transform();

        switch (command.getCommand()) {
            case LEFT:
                if (cam != null) {
                    trans.rotate(-90);
                    entity.add(new Animation(Animation.AnimationType.ROTATE, rotateFrames));
                } else {
                    trans.addX(-1);
                    entity.add(new Animation(Animation.AnimationType.TRANSLATE, translateFrames));
                }
                break;
            case RIGHT:
                if (cam != null) {
                    trans.rotate(90);
                    entity.add(new Animation(Animation.AnimationType.ROTATE, rotateFrames));
                } else {
                    trans.addX(1);
                    entity.add(new Animation(Animation.AnimationType.TRANSLATE, translateFrames));
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
                entity.add(new Animation(Animation.AnimationType.TRANSLATE, translateFrames));
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
                entity.add(new Animation(Animation.AnimationType.TRANSLATE, translateFrames));
                break;
        }

        entity.remove(Command.class);
        entity.add(trans);
    }

    @Override
    public boolean checkProcessing() {
        return !((GobsEngine) getEngine()).isRendering() && super.checkProcessing();
    }
}
