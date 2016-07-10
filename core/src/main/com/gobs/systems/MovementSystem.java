package com.gobs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.gobs.GameState;
import com.gobs.components.Camera;
import com.gobs.components.Command;
import com.gobs.components.Command.CommandType;
import com.gobs.components.Transform;

public class MovementSystem extends EntityProcessingSystem {
    private ComponentMapper<Command> cm = ComponentMapper.getFor(Command.class);
    private ComponentMapper<Camera> km = ComponentMapper.getFor(Camera.class);

    public MovementSystem() {
        this(0);
    }

    public MovementSystem(int priority) {
        super(Family.all(Command.class).get(), priority);
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : getEntities()) {
            Command command = cm.get(entity);
            Camera cam = km.get(entity);

            Transform trans = new Transform();

            if (command.getCommand() == CommandType.LEFT) {
                if (cam != null && cam.getState() == GameState.getState()) {
                    switch (cam.getOrientation()) {
                        case UP:
                            cam.setOrientation(Camera.Orientation.LEFT);
                            break;
                        case DOWN:
                            cam.setOrientation(Camera.Orientation.RIGHT);
                            break;
                        case RIGHT:
                            cam.setOrientation(Camera.Orientation.UP);
                            break;
                        case LEFT:
                            cam.setOrientation(Camera.Orientation.DOWN);
                            break;
                    }
                } else {
                    trans.addX(-1);
                }
            }

            if (command.getCommand() == CommandType.RIGHT) {
                if (cam != null && cam.getState() == GameState.getState()) {
                    switch (cam.getOrientation()) {
                        case UP:
                            cam.setOrientation(Camera.Orientation.RIGHT);
                            break;
                        case DOWN:
                            cam.setOrientation(Camera.Orientation.LEFT);
                            break;
                        case RIGHT:
                            cam.setOrientation(Camera.Orientation.DOWN);
                            break;
                        case LEFT:
                            cam.setOrientation(Camera.Orientation.UP);
                            break;
                    }
                } else {
                    trans.addX(1);
                }
            }
            if (command.getCommand() == CommandType.UP) {
                if (cam != null && cam.getState() == GameState.getState()) {
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
            }
            if (command.getCommand() == CommandType.DOWN) {
                if (cam != null && cam.getState() == GameState.getState()) {
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
            }

            entity.remove(Command.class);
            entity.add(trans);
        }
    }

    @Override
    public void dispose() {
    }
}
