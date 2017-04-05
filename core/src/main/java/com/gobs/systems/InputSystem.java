package com.gobs.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.gobs.GobsEngine;
import com.gobs.components.AI;
import com.gobs.components.Controller;
import com.gobs.components.Goal;
import com.gobs.components.Position;
import com.gobs.display.MapDisplay;
import com.gobs.input.ContextManager;
import com.gobs.input.ContextManager.ContextType;
import com.gobs.input.InputHandler;
import com.gobs.ui.GUI;
import com.gobs.ui.InputMap;
import java.util.List;

public class InputSystem extends EntitySystem {
    private MapDisplay display;
    private InputHandler inputHandler;
    private ContextManager contextManager;
    
    private Family family;
    private ImmutableArray<Entity> entities;

    private final String consummerID = "runtime";

    private final ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
    private final ComponentMapper<Controller> cm = ComponentMapper.getFor(Controller.class);
    private final ComponentMapper<AI> am = ComponentMapper.getFor(AI.class);

    public InputSystem(MapDisplay display, InputHandler inputHandler, ContextManager contextManager) {
        this(display, inputHandler, contextManager, 0);
    }

    public InputSystem(MapDisplay display, InputHandler inputHandler, ContextManager contextManager, int priority) {
        this.family = Family.one(Controller.class, AI.class).get();

        this.display = display;
        this.inputHandler = inputHandler;
        this.contextManager = contextManager;

        registerActions();
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        entities = engine.getEntitiesFor(family);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);

        entities = null;
    }

    @Override
    public void update(float deltaTime) {
        InputMap inputMap = inputHandler.getInputMap();

        if (inputMap.hasInput()) {
            GUI.acceptInput(inputMap);
            contextManager.acceptInput(inputMap);
            contextManager.dispatchInput(inputMap);
            processInputs();
        }

        inputMap.done();
    }

    @Override
    public boolean checkProcessing() {
        return !((GobsEngine) getEngine()).isRendering() && super.checkProcessing();
    }

    private void processInputs() {
        List<ContextManager.Event> events = contextManager.pollActions(consummerID);

        for (ContextManager.Event event : events) {
            switch (event.getAction()) {
                case EXIT:
                    Gdx.app.exit();
                    break;
                case DUMP:
                    dumpEntities();
            }
        }
    }

    private void registerActions() {
        contextManager.registerConsumer(consummerID, ContextType.GLOBAL, ContextManager.Action.EXIT);
        contextManager.registerConsumer(consummerID, ContextType.GLOBAL, ContextManager.Action.DUMP);

        contextManager.registerAction(ContextType.MAP, ContextManager.Action.SCROLL_UP, (action) -> {
            display.getCamera().translate(0, 1);
        });

        contextManager.registerAction(ContextType.MAP, ContextManager.Action.SCROLL_DOWN, (action) -> {
            display.getCamera().translate(0, -1);
        });

        contextManager.registerAction(ContextType.MAP, ContextManager.Action.SCROLL_LEFT, (action) -> {
            display.getCamera().translate(-1, 0);
        });

        contextManager.registerAction(ContextType.MAP, ContextManager.Action.SCROLL_RIGHT, (action) -> {
            display.getCamera().translate(1, 0);
        });

        contextManager.registerAction(ContextType.MAP, ContextManager.Action.ZOOM_IN, (action) -> {
            display.getCamera().zoom -= 0.1f;
        });

        contextManager.registerAction(ContextType.MAP, ContextManager.Action.ZOOM_OUT, (action) -> {
            display.getCamera().zoom += 0.1f;
        });

        contextManager.registerAction(ContextType.EDITMAP, ContextManager.Action.TARGET, (action) -> {
            setTarget();
        });
    }

    private void dumpEntities() {
        int i = 0;
        for (Entity entity : getEngine().getEntities()) {
            System.out.println("=== Entity #" + i + " ===");
            for (Component component : entity.getComponents()) {
                System.out.println(" * " + component.getClass().getSimpleName());
            }
            i++;
        }
    }

    private void setTarget() {
        int x = 0;
        int y = 0;
        for (Entity entity : entities) {
            Controller controller = cm.get(entity);
            Position pos = pm.get(entity);

            if (controller != null && controller.isActive() && pos != null) {
                x = pos.getX();
                y = pos.getY();
                System.out.println("Target");
                break;
            }
        }

        for (Entity entity : entities) {
            AI ai = am.get(entity);

            if (ai != null) {
                entity.add(new Goal(x, y));
                break;
            }
        }
    }
}
