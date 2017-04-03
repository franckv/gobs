package com.gobs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.gobs.components.Command;
import com.gobs.components.Controller;
import com.gobs.input.ContextManager;
import java.util.List;

public class ControllerSystem extends EntityProcessingSystem {
    private ContextManager contextManager;

    private ComponentMapper<Controller> cm = ComponentMapper.getFor(Controller.class);

    private String consummerID = "controller";

    public ControllerSystem(ContextManager contextManager) {
        this(contextManager, 0);
    }

    public ControllerSystem(ContextManager contextManager, int priority) {
        super(Family.one(Controller.class).get(), priority);

        this.contextManager = contextManager;

        registerActions();
    }

    @Override
    public void update(float deltaTime) {
        processInputs();
    }

    private void processInputs() {
        List<ContextManager.Action> actions = contextManager.pollActions(consummerID);

        for (ContextManager.Action action : actions) {
            switch (action) {
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
        contextManager.registerConsumer(consummerID, ContextManager.Action.MOVE_UP);
        contextManager.registerConsumer(consummerID, ContextManager.Action.MOVE_DOWN);
        contextManager.registerConsumer(consummerID, ContextManager.Action.MOVE_LEFT);
        contextManager.registerConsumer(consummerID, ContextManager.Action.MOVE_RIGHT);
    }

    private boolean setCommand(Command.CommandType type) {
        Command command = new Command(type);
        for (Entity entity : getEntities()) {
            Controller controller = cm.get(entity);

            if (controller.isActive()) {
                entity.add(command);
                return true;
            }
        }

        return false;
    }

    @Override
    public void dispose() {
    }

}
