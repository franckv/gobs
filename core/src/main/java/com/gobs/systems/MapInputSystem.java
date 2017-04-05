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
import com.gobs.map.Layer;
import com.gobs.map.LayerCell;
import java.util.List;

public class MapInputSystem extends EntitySystem {
    private ContextManager contextManager;
    private Family family;
    private ImmutableArray<Entity> entities;
    private StateManager stateManager;
    private Layer mapLayer;

    private final ComponentMapper<Controller> cm = ComponentMapper.getFor(Controller.class);
    private final ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
    private final ComponentMapper<Hidden> hm = ComponentMapper.getFor(Hidden.class);

    private final String consummerID = "map";

    public MapInputSystem(ContextManager contextManager, StateManager stateManager, Layer mapLayer) {
        this(contextManager, stateManager, mapLayer, 0);
    }

    public MapInputSystem(ContextManager contextManager, StateManager stateManager, Layer mapLayer, int priority) {
        this.family = Family.all(Controller.class, Position.class).get();

        this.contextManager = contextManager;
        this.stateManager = stateManager;
        this.mapLayer = mapLayer;

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
                case DIG:
                    digMap();
                    break;
                case FILL:
                    fillMap();
                    break;
                case TOGGLE_EDIT:
                    if (event.getContext() == ContextType.MAP) {
                        stateManager.setState(StateManager.State.EDITMAP);
                        editMap(true);
                    } else {
                        stateManager.setState(StateManager.State.MAP);
                        editMap(false);
                    }
                    break;
                case TOGGLE_VIEW:
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

        contextManager.registerAction(ContextType.EDITMAP, ContextManager.Action.TOGGLE_VIEW, (action) -> {
            stateManager.setState(StateManager.State.CRAWL);
            editMap(false);
        });

        contextManager.registerAction(ContextType.CRAWLING, ContextManager.Action.TOGGLE_VIEW, (action) -> {
            stateManager.setState(StateManager.State.MAP);
        });

        contextManager.registerAction(ContextType.MAP, ContextManager.Action.TOGGLE_VIEW, (action) -> {
            stateManager.setState(StateManager.State.CRAWL);
        });

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
        mapLayer.setCell(x, y, LayerCell.LayerCellType.FLOOR, false);
        mapLayer.setDirty(true);
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
        mapLayer.setCell(x, y, LayerCell.LayerCellType.WALL, true);
        mapLayer.setDirty(true);
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
