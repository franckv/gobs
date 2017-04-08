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
import com.gobs.components.WorkItem;
import com.gobs.input.ContextManager;
import com.gobs.input.ContextManager.Event;
import java.util.List;

public class DesignationSystem extends EntitySystem {
    private ContextManager contextManager;
    private StateManager stateManager;

    private Family designationFamily;
    private Family cursorFamily;
    private ImmutableArray<Entity> designations;
    private ImmutableArray<Entity> cursor;

    private final static String consummerID = DesignationSystem.class.getName();

    private final ComponentMapper<Designation> dm = ComponentMapper.getFor(Designation.class);
    private final ComponentMapper<Controller> cm = ComponentMapper.getFor(Controller.class);
    private final ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);

    public DesignationSystem(ContextManager contextManager, StateManager stateManager) {
        this(contextManager, stateManager, 0);
    }

    public DesignationSystem(ContextManager contextManager, StateManager stateManager, int priority) {
        super(priority);

        this.designationFamily = Family.all(Designation.class, Pending.class).get();
        this.cursorFamily = Family.all(Position.class, Controller.class).exclude(Animation.class).get();

        this.contextManager = contextManager;
        this.stateManager = stateManager;

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

    private void registerActions() {
        contextManager.registerConsumer(consummerID, ContextManager.ContextType.EDITMAP, ContextManager.Action.COMPLETE);
        contextManager.registerConsumer(consummerID, ContextManager.ContextType.EDITMAP, ContextManager.Action.DIG);
        contextManager.registerConsumer(consummerID, ContextManager.ContextType.EDITMAP, ContextManager.Action.FILL);
    }

    private void processInputs() {
        List<Event> events = contextManager.pollActions(consummerID);

        for (Event event : events) {
            switch (event.getAction()) {
                case DIG:
                    startDesignation(WorkItem.WorkType.DIGGING);
                    break;
                case FILL:
                    startDesignation(WorkItem.WorkType.FILLING);
                    break;
                case COMPLETE:
                    completeDesignation();
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

            int width = pos.getX() - design.getX();
            int height = pos.getY() - design.getY();

            if (width >= 0) {
                width += 1;
            } else {
                width -= 1;
            }
            if (height >= 0) {
                height += 1;
            } else {
                height -= 1;
            }

            design.setWidth(width);
            design.setHeight(height);
        }
    }

    private void cancelDesignation() {
        for (Entity entity : designations) {
            System.out.println("cancel");
            getEngine().removeEntity(entity);
        }
    }

    private void startDesignation(WorkItem.WorkType type) {
        // only one designation at a time
        if (designations.size() > 0) {
            return;
        }

        Entity cursor = getCursor();

        if (cursor != null) {
            Position pos = pm.get(cursor);

            Entity zone = new Entity();
            zone.add(new Designation(pos.getX(), pos.getY()));
            zone.add(new WorkItem(type, 10));
            zone.add(new Pending());
            getEngine().addEntity(zone);
        }
    }

    private void completeDesignation() {
        for (Entity entity : designations) {
            System.out.println("complete");
            Designation design = dm.get(entity);
            normalize(design);

            entity.remove(Pending.class);
        }
    }

    private void normalize(Designation designation) {
        int x = designation.getX();
        int y = designation.getY();
        int width = designation.getWidth();
        int height = designation.getHeight();

        x = Math.min(x, x + width + 1);
        y = Math.min(y, y + height + 1);
        width = Math.abs(width);
        height = Math.abs(height);

        designation.setPosition(x, y);
        designation.setDimension(width, height);

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
