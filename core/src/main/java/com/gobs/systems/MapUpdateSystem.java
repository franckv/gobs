package com.gobs.systems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.utils.Array;
import com.gobs.StateManager;
import com.gobs.components.Controller;
import com.gobs.components.Hidden;
import com.gobs.components.Position;
import com.gobs.input.ContextManager;
import com.gobs.input.ContextManager.ContextType;
import com.gobs.input.ContextManager.Event;
import com.gobs.map.WorldMap;

public class MapUpdateSystem extends BaseEntitySystem {
    private ComponentMapper<Controller> cm;
    private ComponentMapper<Hidden> hm;

    @Wire
    private ContextManager contextManager;
    @Wire
    private StateManager stateManager;
    @Wire
    private WorldMap worldMap;

    private final static String consummerID = MapUpdateSystem.class.getName();

    public MapUpdateSystem() {
        super(Aspect.all(Controller.class, Position.class));
    }

    @Override
    protected void initialize() {
        registerActions();
    }

    @Override
    protected void processSystem() {
        worldMap.getCurrentLevel().setDirty(false);
        processInputs();
    }

    private void processInputs() {
        Array<Event> events = contextManager.pollActions(consummerID);

        for (Event event : events) {
            switch (event.getAction()) {
                case TOGGLE_EDIT:
                    worldMap.getCurrentLevel().setDirty(true);

                    if (event.getContext() == ContextType.MAP) {
                        stateManager.setState(StateManager.State.EDITMAP);
                        editMap(true);
                    } else {
                        stateManager.setState(StateManager.State.MAP);
                        editMap(false);
                    }
                    break;
                case TOGGLE_VIEW:
                    worldMap.getCurrentLevel().setDirty(true);

                    if (event.getContext() == ContextType.CRAWLING) {
                        stateManager.setState(StateManager.State.MAP);
                    } else if (event.getContext() == ContextType.EDITMAP) {
                        stateManager.setState(StateManager.State.CRAWL);
                        editMap(false);
                    } else {
                        stateManager.setState(StateManager.State.CRAWL);
                    }
                    break;
            }
        }
    }

    private void registerActions() {
        contextManager.registerConsumer(consummerID, ContextType.EDITMAP, ContextManager.Action.TOGGLE_EDIT);
        contextManager.registerConsumer(consummerID, ContextType.EDITMAP, ContextManager.Action.TOGGLE_VIEW);
        contextManager.registerConsumer(consummerID, ContextType.CRAWLING, ContextManager.Action.TOGGLE_VIEW);
        contextManager.registerConsumer(consummerID, ContextType.MAP, ContextManager.Action.TOGGLE_VIEW);
        contextManager.registerConsumer(consummerID, ContextType.MAP, ContextManager.Action.TOGGLE_EDIT);
    }

    private void editMap(boolean edit) {
        for (int i = 0; i < getEntityIds().size(); i++) {
            int entityId = getEntityIds().get(i);

            Controller controller = cm.get(entityId);
            Hidden hidden = hm.get(entityId);

            if (edit) {
                if (controller.isActive()) {
                    controller.setActive(false);
                } else {
                    System.out.println("Show cursor");
                    controller.setActive(true);
                    if (hidden != null) {
                        hm.remove(entityId);
                    }
                }
            } else {
                if (controller.isActive()) {
                    System.out.println("Hide cursor");
                    controller.setActive(false);
                    hm.create(entityId);
                } else {
                    controller.setActive(true);
                }
            }
        }
    }
}
