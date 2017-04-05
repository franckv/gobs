package com.gobs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.gobs.GobsEngine;
import com.gobs.components.Command;
import com.gobs.components.Controller;
import com.gobs.input.ContextManager;
import com.gobs.input.ContextManager.ContextType;
import java.util.List;

public class ControllerSystem extends EntitySystem {
    private ContextManager contextManager;
    private Family family;
    private ImmutableArray<Entity> entities;

    private ComponentMapper<Controller> cm = ComponentMapper.getFor(Controller.class);

    private String consummerID = "controller";

    public ControllerSystem(ContextManager contextManager) {
        this(contextManager, 0);
    }

    public ControllerSystem(ContextManager contextManager, int priority) {
        this.family = Family.one(Controller.class).get();

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
        processInputs();
    }

    @Override
    public boolean checkProcessing() {
        return !((GobsEngine) getEngine()).isRendering() && super.checkProcessing();
    }

    private void processInputs() {
        List<ContextManager.Event> events = contextManager.pollActions(consummerID);

        for (ContextManager.Event event : events) {
            switch (event.getAction()) {
                case MOVE_UP:
                    setCommand(Command.CommandType.UP);
                    break;
                case MOVE_DOWN:
                    setCommand(Command.CommandType.DOWN);
                    break;
                case MOVE_LEFT:
                    setCommand(Command.CommandType.LEFT);
                    break;
                case MOVE_RIGHT:
                    setCommand(Command.CommandType.RIGHT);
                    break;
            }
        }
    }

    private void registerActions() {
        contextManager.registerConsumer(consummerID, ContextType.CRAWLING, ContextManager.Action.MOVE_UP);
        contextManager.registerConsumer(consummerID, ContextType.CRAWLING, ContextManager.Action.MOVE_DOWN);
        contextManager.registerConsumer(consummerID, ContextType.CRAWLING, ContextManager.Action.MOVE_LEFT);
        contextManager.registerConsumer(consummerID, ContextType.CRAWLING, ContextManager.Action.MOVE_RIGHT);

        contextManager.registerConsumer(consummerID, ContextType.EDITMAP, ContextManager.Action.MOVE_UP);
        contextManager.registerConsumer(consummerID, ContextType.EDITMAP, ContextManager.Action.MOVE_DOWN);
        contextManager.registerConsumer(consummerID, ContextType.EDITMAP, ContextManager.Action.MOVE_LEFT);
        contextManager.registerConsumer(consummerID, ContextType.EDITMAP, ContextManager.Action.MOVE_RIGHT);
    }

    private boolean setCommand(Command.CommandType type) {
        Command command = new Command(type);
        for (Entity entity : entities) {
            Controller controller = cm.get(entity);

            if (controller.isActive()) {
                entity.add(command);
                return true;
            }
        }

        return false;
    }
}
