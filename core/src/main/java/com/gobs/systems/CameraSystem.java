package com.gobs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.gobs.GobsEngine;
import com.gobs.components.Camera;
import com.gobs.components.Position;
import com.gobs.display.DisplayManager;
import com.gobs.display.PerspectiveDisplay;
import com.gobs.input.ContextManager;
import java.util.List;

/**
 *
 */
public class CameraSystem extends IteratingSystem {
    private ComponentMapper<Camera> cm = ComponentMapper.getFor(Camera.class);
    private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);

    private final static String consummerID = CameraSystem.class.getName();

    private DisplayManager displayManager;
    private ContextManager contextManager;

    public CameraSystem(DisplayManager displayManager, ContextManager contextManager) {
        this(displayManager, contextManager, 0);
    }

    public CameraSystem(DisplayManager displayManager, ContextManager contextManager, int priority) {
        super(Family.all(Camera.class, Position.class).get(), priority);

        this.displayManager = displayManager;
        this.contextManager = contextManager;

        registerActions();
    }

    @Override
    public void update(float deltaTime) {
        processInputs();

        super.update(deltaTime);
    }

    @Override
    public boolean checkProcessing() {
        return !((GobsEngine) getEngine()).isRendering() && super.checkProcessing();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Camera cam = cm.get(entity);
        Position pos = pm.get(entity);
  
        PerspectiveDisplay display = displayManager.getFPVDisplay();
        PerspectiveCamera camera = display.getCamera();
        float step = display.getStepSize();
        
        float x = pos.getX() + pos.getDX();
        float y = pos.getY() + pos.getDY();
        float angle = (float) Math.toRadians(cam.getRotation() + cam.getAngle());

        camera.position.set(x * step, y * step, 0f);

        float dx = (float) Math.sin(angle);
        float dy = (float) Math.cos(angle);

        camera.lookAt((x + dx) * step, (y + dy) * step, 0f);
    }

    private void processInputs() {
        List<ContextManager.Event> events = contextManager.pollActions(consummerID);

        for (ContextManager.Event event : events) {
            switch (event.getAction()) {
                case SCROLL_UP:
                    displayManager.getMapDisplay().getCamera().translate(0, 1);
                    break;
                case SCROLL_DOWN:
                    displayManager.getMapDisplay().getCamera().translate(0, -1);
                    break;
                case SCROLL_LEFT:
                    displayManager.getMapDisplay().getCamera().translate(-1, 0);
                    break;
                case SCROLL_RIGHT:
                    displayManager.getMapDisplay().getCamera().translate(1, 0);
                    break;
                case ZOOM_IN:
                    displayManager.getMapDisplay().getCamera().zoom -= 0.1f;
                    break;
                case ZOOM_OUT:
                    displayManager.getMapDisplay().getCamera().zoom += 0.1f;
                    break;
            }
        }
    }

    private void registerActions() {
        contextManager.registerConsumer(consummerID, ContextManager.ContextType.MAP, ContextManager.Action.SCROLL_UP);
        contextManager.registerConsumer(consummerID, ContextManager.ContextType.MAP, ContextManager.Action.SCROLL_DOWN);
        contextManager.registerConsumer(consummerID, ContextManager.ContextType.MAP, ContextManager.Action.SCROLL_LEFT);
        contextManager.registerConsumer(consummerID, ContextManager.ContextType.MAP, ContextManager.Action.SCROLL_RIGHT);
        contextManager.registerConsumer(consummerID, ContextManager.ContextType.MAP, ContextManager.Action.ZOOM_IN);
        contextManager.registerConsumer(consummerID, ContextManager.ContextType.MAP, ContextManager.Action.ZOOM_OUT);
    }
}
