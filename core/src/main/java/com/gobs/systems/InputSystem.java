package com.gobs.systems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.gobs.components.AI;
import com.gobs.components.Controller;
import com.gobs.components.Goal;
import com.gobs.components.Position;
import com.gobs.input.ContextManager;
import com.gobs.input.ContextManager.ContextType;
import com.gobs.input.InputHandler;
import com.gobs.ui.GUI;
import com.gobs.ui.InputMap;

public class InputSystem extends BaseEntitySystem {
    private ComponentMapper<Position> pm;
    private ComponentMapper<Goal> gm;
    private ComponentMapper<Controller> cm;
    private ComponentMapper<AI> am;

    @Wire
    private InputHandler inputHandler;
    @Wire
    private ContextManager contextManager;

    private final static String consummerID = InputSystem.class.getName();

    public InputSystem() {
        super(Aspect.one(Controller.class, AI.class));
    }

    @Override
    protected void initialize() {
        registerActions();
    }

    @Override
    protected void processSystem() {
        InputMap inputMap = inputHandler.getInputMap();

        if (inputMap.hasInput()) {
            GUI.acceptInput(inputMap);
            contextManager.acceptInput(inputMap);
            contextManager.dispatchInput(inputMap);
            processInputs();
        }

        inputMap.done();
    }

    private void processInputs() {
        Array<ContextManager.Event> events = contextManager.pollActions(consummerID);

        for (ContextManager.Event event : events) {
            switch (event.getAction()) {
                case EXIT:
                    Gdx.app.exit();
                    break;
                case DUMP:
                    dumpEntities();
                    break;
                case TARGET:
                    setTarget();
                    break;
            }
        }
    }

    private void registerActions() {
        contextManager.registerConsumer(consummerID, ContextType.GLOBAL, ContextManager.Action.EXIT);
        contextManager.registerConsumer(consummerID, ContextType.GLOBAL, ContextManager.Action.DUMP);
        contextManager.registerConsumer(consummerID, ContextType.EDITMAP, ContextManager.Action.TARGET);
    }

    private void dumpEntities() {
        for (int i = 0; i < getEntityIds().size(); i++) {
            int entityId = getEntityIds().get(i);

            System.out.println("=== Entity #" + i + " ===");

            Bag<Component> components = new Bag<>();
            getWorld().getEntity(entityId).getComponents(components);

            for (Component component : components) {
                System.out.println(" * " + component.getClass().getSimpleName());
            }
        }
    }

    private void setTarget() {
        int x = 0;
        int y = 0;
        for (int i = 0; i < getEntityIds().size(); i++) {
            int entityId = getEntityIds().get(i);

            Controller controller = cm.get(entityId);
            Position pos = pm.get(entityId);

            if (controller != null && controller.isActive() && pos != null) {
                x = pos.getX();
                y = pos.getY();
                System.out.println("Target");
                break;
            }
        }

        for (int i = 0; i < getEntityIds().size(); i++) {
            int entityId = getEntityIds().get(i);

            AI ai = am.get(entityId);

            if (ai != null) {
                Goal goal = gm.create(entityId);
                goal.setPosition(x, y);
                break;
            }
        }
    }
}
