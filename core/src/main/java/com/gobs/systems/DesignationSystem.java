package com.gobs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.gobs.StateManager;
import com.gobs.components.Animation;
import com.gobs.components.Controller;
import com.gobs.components.Designation;
import com.gobs.components.Pending;
import com.gobs.components.Position;
import com.gobs.input.ContextManager;
import com.gobs.input.ContextManager.Event;
import com.gobs.map.LevelCell;
import com.gobs.map.WorldMap;
import java.util.List;

public class DesignationSystem extends EntitySystem {
    private ContextManager contextManager;
    private StateManager stateManager;
    private WorldMap worldMap;

    private Family designationFamily;
    private Family cursorFamily;
    private ImmutableArray<Entity> designations;
    private ImmutableArray<Entity> cursor;

    private final static String consummerID = DesignationSystem.class.getName();

    private final ComponentMapper<Designation> dm = ComponentMapper.getFor(Designation.class);
    private final ComponentMapper<Controller> cm = ComponentMapper.getFor(Controller.class);
    private final ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);

    public DesignationSystem(ContextManager contextManager, StateManager stateManager, WorldMap worldMap) {
        this(contextManager, stateManager, worldMap, 0);
    }

    public DesignationSystem(ContextManager contextManager, StateManager stateManager, WorldMap worldMap, int priority) {
        super(priority);

        this.designationFamily = Family.all(Designation.class, Pending.class).get();
        this.cursorFamily = Family.all(Position.class, Controller.class).exclude(Animation.class).get();

        this.contextManager = contextManager;
        this.stateManager = stateManager;
        this.worldMap = worldMap;

        registerActions();
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        designations = engine.getEntitiesFor(designationFamily);
        cursor = engine.getEntitiesFor(cursorFamily);

    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);

        designations = null;
        cursor = null;
    }

    @Override
    public void update(float deltaTime) {
        if (stateManager.getState() == StateManager.State.EDITMAP) {
            updateDesignation();
        } else {
            cancelDesignation();
        }
        processInputs();
    }

    private void processInputs() {
        List<Event> events = contextManager.pollActions(consummerID);

        for (Event event : events) {
            switch (event.getAction()) {
                case DIG:
                    digMap();
                    break;
                case FILL:
                    fillMap();
                    break;
                case DESIGNATE:
                    toggleDesignation();
                    break;
            }
        }

    }

    private void updateDesignation() {
        Entity cursor = getCursor();

        if (cursor == null) {
            return;
        }

        Position pos = pm.get(cursor);

        for (Entity entity : designations) {
            Designation design = dm.get(entity);

            design.setWidth(pos.getX() - design.getX());
            design.setHeight(pos.getY() - design.getY());
        }
    }

    private void cancelDesignation() {
        for (Entity entity : designations) {
            System.out.println("cancel");
            getEngine().removeEntity(entity);
        }
    }

    private void toggleDesignation() {
        if (designations.size() > 0) {
            for (Entity entity : designations) {
                System.out.println("complete");
                entity.remove(Pending.class);
            }
        } else {
            Entity cursor = getCursor();

            if (cursor != null) {
                Position pos = pm.get(cursor);

                System.out.println("Start designation at " + pos.getX() + "," + pos.getY());

                Designation design = new Designation(pos.getX(), pos.getY());

                Entity zone = new Entity();
                zone.add(design);
                zone.add(new Pending());

                getEngine().addEntity(zone);
            }
        }
    }

    private void digMap() {
        Entity cursor = getCursor();

        if (cursor != null) {
            Position pos = pm.get(cursor);
            System.out.println("Dig at " + pos.getX() + "," + pos.getY());
            worldMap.getCurrentLevel().setCell(pos.getX(), pos.getY(), LevelCell.LevelCellType.FLOOR);
        }
    }

    private void fillMap() {
        Entity cursor = getCursor();

        if (cursor != null) {
            Position pos = pm.get(cursor);
            System.out.println("Fill " + pos.getX() + "," + pos.getY());
            worldMap.getCurrentLevel().setCell(pos.getX(), pos.getY(), LevelCell.LevelCellType.WALL);
        }
    }

    private void registerActions() {
        contextManager.registerConsumer(consummerID, ContextManager.ContextType.EDITMAP, ContextManager.Action.DESIGNATE);
        contextManager.registerConsumer(consummerID, ContextManager.ContextType.EDITMAP, ContextManager.Action.DIG);
        contextManager.registerConsumer(consummerID, ContextManager.ContextType.EDITMAP, ContextManager.Action.FILL);
    }

    private Entity getCursor() {
        for (Entity entity : cursor) {
            Controller controller = cm.get(entity);

            if (controller.isActive()) {
                return entity;
            }
        }

        return null;
    }
}
