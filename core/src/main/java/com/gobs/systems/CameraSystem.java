package com.gobs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.Array;
import com.gobs.components.Camera;
import com.gobs.components.Position;
import com.gobs.display.DisplayManager;
import com.gobs.display.PerspectiveDisplay;
import com.gobs.input.ContextManager;

public class CameraSystem extends IteratingSystem {
    private ComponentMapper<Camera> cm;
    private ComponentMapper<Position> pm;

    @Wire
    private DisplayManager displayManager;
    @Wire
    private ContextManager contextManager;

    private final static String consummerID = CameraSystem.class.getName();

    public CameraSystem() {
        super(Aspect.all(Camera.class, Position.class));
    }

    @Override
    protected void initialize() {
        registerActions();
    }

    @Override
    protected void begin() {
        processInputs();
    }

    @Override
    protected void process(int entityId) {
        Camera cam = cm.get(entityId);
        Position pos = pm.get(entityId);

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
        Array<ContextManager.Event> events = contextManager.pollActions(consummerID);

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
