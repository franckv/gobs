package com.gobs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.gobs.GobsEngine;
import com.gobs.StateManager;
import com.gobs.components.Controller;
import com.gobs.components.Hidden;
import com.gobs.components.Position;
import com.gobs.input.ContextManager;
import com.gobs.input.ContextManager.ContextType;
import com.gobs.map.LayerCell;
import com.gobs.map.WorldMap;
import java.util.List;

public class MapUpdateSystem extends EntitySystem {
    private ContextManager contextManager;
    private Family family;
    private ImmutableArray<Entity> entities;
    private StateManager stateManager;
    private WorldMap worldMap;

    private final ComponentMapper<Controller> cm = ComponentMapper.getFor(Controller.class);
    private final ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
    private final ComponentMapper<Hidden> hm = ComponentMapper.getFor(Hidden.class);

    private final static String consummerID = "map";

    public MapUpdateSystem(ContextManager contextManager, StateManager stateManager, WorldMap worldMap) {
        this(contextManager, stateManager, worldMap, 0);
    }

    public MapUpdateSystem(ContextManager contextManager, StateManager stateManager, WorldMap worldMap, int priority) {
        this.family = Family.all(Controller.class, Position.class).get();

        this.contextManager = contextManager;
        this.stateManager = stateManager;
        this.worldMap = worldMap;

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
        worldMap.getCurrentLayer().setDirty(false);
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
                case DIG:
                    digMap();
                    break;
                case FILL:
                    fillMap();
                    break;
                case TOGGLE_EDIT:
                    worldMap.getCurrentLayer().setDirty(true);

                    if (event.getContext() == ContextType.MAP) {
                        stateManager.setState(StateManager.State.EDITMAP);
                        editMap(true);
                    } else {
                        stateManager.setState(StateManager.State.MAP);
                        editMap(false);
                    }
                    break;
                case TOGGLE_VIEW:
                    worldMap.getCurrentLayer().setDirty(true);

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
        contextManager.registerConsumer(consummerID, ContextType.EDITMAP, ContextManager.Action.DIG);
        contextManager.registerConsumer(consummerID, ContextType.EDITMAP, ContextManager.Action.FILL);
        contextManager.registerConsumer(consummerID, ContextType.MAP, ContextManager.Action.TOGGLE_EDIT);
        contextManager.registerConsumer(consummerID, ContextType.EDITMAP, ContextManager.Action.TOGGLE_EDIT);
        contextManager.registerConsumer(consummerID, ContextType.EDITMAP, ContextManager.Action.TOGGLE_VIEW);
        contextManager.registerConsumer(consummerID, ContextType.CRAWLING, ContextManager.Action.TOGGLE_VIEW);
        contextManager.registerConsumer(consummerID, ContextType.MAP, ContextManager.Action.TOGGLE_VIEW);
    }

    private void digMap() {
        int x = 0;
        int y = 0;
        for (Entity entity : entities) {
            Controller controller = cm.get(entity);
            Position pos = pm.get(entity);

            if (controller.isActive()) {
                x = pos.getX();
                y = pos.getY();
                break;
            }
        }

        System.out.println("Dig at " + x + "," + y);
        worldMap.getCurrentLayer().setCell(x, y, LayerCell.LayerCellType.FLOOR, false);
    }

    private void fillMap() {
        int x = 0;
        int y = 0;
        for (Entity entity : entities) {
            Controller controller = cm.get(entity);
            Position pos = pm.get(entity);

            if (controller.isActive()) {
                x = pos.getX();
                y = pos.getY();
                break;
            }
        }

        System.out.println("Fill " + x + "," + y);
        worldMap.getCurrentLayer().setCell(x, y, LayerCell.LayerCellType.WALL, true);
    }

    private void editMap(boolean edit) {
        for (Entity entity : entities) {
            Controller controller = cm.get(entity);
            Hidden hidden = hm.get(entity);

            if (edit) {
                if (controller.isActive()) {
                    controller.setActive(false);
                } else {
                    System.out.println("Show cursor");
                    controller.setActive(true);
                    if (hidden != null) {
                        entity.remove(Hidden.class);
                    }
                }
            } else {
                if (controller.isActive()) {
                    System.out.println("Hide cursor");
                    controller.setActive(false);
                    entity.add(new Hidden());
                } else {
                    controller.setActive(true);
                }
            }
        }
    }
}
