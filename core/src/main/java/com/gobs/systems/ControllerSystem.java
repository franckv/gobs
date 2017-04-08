package com.gobs.systems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.utils.Array;
import com.gobs.components.Animation;
import com.gobs.components.Command;
import com.gobs.components.Controller;
import com.gobs.input.ContextManager;
import com.gobs.input.ContextManager.ContextType;

public class ControllerSystem extends BaseEntitySystem {
    private ComponentMapper<Controller> cm;
    private ComponentMapper<Command> mm;

    @Wire
    private ContextManager contextManager;

    private String consummerID = ControllerSystem.class.getName();

    public ControllerSystem() {
        super(Aspect.all(Controller.class).exclude(Animation.class));
    }

    @Override
    protected void initialize() {
        registerActions();
    }

    @Override
    protected void processSystem() {
        processInputs();
    }

    private void processInputs() {
        Array<ContextManager.Event> events = contextManager.pollActions(consummerID);

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
        for (int i = 0; i < getEntityIds().size(); i++) {
            int entityId = getEntityIds().get(i);

            Controller controller = cm.get(entityId);

            if (controller.isActive()) {
                Command command = mm.create(entityId);
                command.setCommand(type);
                return true;
            }
        }

        return false;
    }
}
