package com.gobs.systems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Array;
import com.gobs.StateManager;
import com.gobs.components.Animation;
import com.gobs.components.Controller;
import com.gobs.components.Designation;
import com.gobs.components.Pending;
import com.gobs.components.Position;
import com.gobs.components.WorkItem;
import com.gobs.input.ContextManager;
import com.gobs.input.ContextManager.Event;

public class DesignationSystem extends BaseEntitySystem {
    private ComponentMapper<Designation> dm;
    private ComponentMapper<Controller> cm;
    private ComponentMapper<Position> pm;
    private ComponentMapper<Pending> gm;
    private ComponentMapper<WorkItem> wm;

    @Wire
    private ContextManager contextManager;
    @Wire
    private StateManager stateManager;

    private Position cursorPosition = null;

    private final static String consummerID = DesignationSystem.class.getName();

    public DesignationSystem() {
        super(Aspect.all(Designation.class, Pending.class));
    }

    @Override
    protected void initialize() {
        registerActions();
    }

    @Override
    public void begin() {
        cursorPosition = null;

        IntBag cursor = getWorld().getAspectSubscriptionManager().get(Aspect.all(Position.class, Controller.class).exclude(Animation.class)).getEntities();

        for (int i = 0; i < cursor.size(); i++) {
            int entityId = cursor.get(i);

            Position pos = pm.get(entityId);
            Controller controller = cm.get(entityId);

            if (controller.isActive()) {
                cursorPosition = pos;
                break;
            }
        }
    }

    @Override
    protected void processSystem() {
        if (cursorPosition == null) {
            return;
        }
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
        Array<Event> events = contextManager.pollActions(consummerID);

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
        for (int i = 0; i < getEntityIds().size(); i++) {
            int entityId = getEntityIds().get(i);

            Designation design = dm.get(entityId);

            int width = cursorPosition.getX() - design.getX();
            int height = cursorPosition.getY() - design.getY();

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
        for (int i = 0; i < getEntityIds().size(); i++) {
            int entityId = getEntityIds().get(i);

            System.out.println("cancel");
            getWorld().delete(entityId);
        }
    }

    private void startDesignation(WorkItem.WorkType type) {
        // only one designation at a time
        if (getEntityIds().size() > 0) {
            return;
        }

        int zone = getWorld().create();

        Designation design = dm.create(zone);
        design.setPosition(cursorPosition.getX(), cursorPosition.getY());

        WorkItem work = wm.create(zone);
        work.setDuration(10);
        work.setType(type);

        gm.create(zone);
    }

    private void completeDesignation() {
        for (int i = 0; i < getEntityIds().size(); i++) {
            int entityId = getEntityIds().get(i);

            System.out.println("complete");
            Designation design = dm.get(entityId);
            normalize(design);

            gm.remove(entityId);
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
}
