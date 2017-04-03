package com.gobs.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.gobs.StateManager;
import com.gobs.components.AI;
import com.gobs.components.Command;
import com.gobs.components.Command.CommandType;
import com.gobs.components.Controller;
import com.gobs.components.Goal;
import com.gobs.components.Hidden;
import com.gobs.components.Position;
import com.gobs.display.MapDisplay;
import com.gobs.input.ContextManager;
import com.gobs.input.ContextManager.Action;
import com.gobs.input.ContextManager.ContextType;
import com.gobs.input.InputHandler;
import com.gobs.map.Layer;
import com.gobs.map.LayerCell.LayerCellType;
import com.gobs.ui.GUI;
import com.gobs.ui.InputMap;
import java.util.List;

public class InputSystem extends EntityProcessingSystem {
    private MapDisplay display;
    private InputHandler inputHandler;
    private ContextManager contextManager;
    private StateManager stateManager;
    private Layer mapLayer;

    int repeat = 0;
    int repeatWait = 20;
    int rate = 1;

    private String consummerID = "runtime";

    private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
    private ComponentMapper<Controller> cm = ComponentMapper.getFor(Controller.class);
    private ComponentMapper<Hidden> hm = ComponentMapper.getFor(Hidden.class);
    private ComponentMapper<AI> am = ComponentMapper.getFor(AI.class);

    public InputSystem(MapDisplay display, InputHandler inputHandler, ContextManager contextManager, StateManager stateManager, Layer mapLayer, int repeat) {
        this(display, inputHandler, contextManager, stateManager, mapLayer, repeat, 0);
    }

    public InputSystem(MapDisplay display, InputHandler inputHandler, ContextManager contextManager, StateManager stateManager, Layer mapLayer, int repeat, int priority) {
        super(Family.one(Controller.class, AI.class).get(), priority);

        this.display = display;
        this.inputHandler = inputHandler;
        this.stateManager = stateManager;
        this.contextManager = contextManager;
        this.mapLayer = mapLayer;

        repeatWait = repeat;

        registerActions();
    }

    @Override
    public void update(float deltaTime) {
        InputMap inputMap = inputHandler.getInputMap();

        GUI.acceptInput(inputMap);

        if (inputHandler.hasInput()) {
            if (inputHandler.hasChanged()) {
                repeat = 0;
                rate = 1;
            }

            if (repeat > repeatWait) {
                repeat = 0;
                rate += 5;
                if (rate > 10) {
                    rate = 10;
                }
            }

            if (repeat == 0) {
                contextManager.acceptInput(inputMap);
                contextManager.dispatchInput(inputMap);
                processInputs();
            }

            repeat += rate;
        } else {
            repeat = 0;
            rate = 1;
        }
    }

    private void processInputs() {
        List<Action> actions = contextManager.pollActions(consummerID);

        for (Action action : actions) {
            switch (action) {
                case EXIT:
                    Gdx.app.exit();
                    break;
                case DUMP:
                    dumpEntities();
            }
        }
    }

    private void registerActions() {
        contextManager.registerConsumer(consummerID, ContextManager.Action.EXIT);
        contextManager.registerConsumer(consummerID, ContextManager.Action.DUMP);

        contextManager.registerAction(ContextType.CRAWLING, ContextManager.Action.TOGGLE_VIEW, (action) -> {
            stateManager.setState(StateManager.State.MAP);
        });

        contextManager.registerAction(ContextType.MAP, ContextManager.Action.TOGGLE_VIEW, (action) -> {
            stateManager.setState(StateManager.State.CRAWL);
        });

        contextManager.registerAction(ContextType.MAP, ContextManager.Action.TOGGLE_EDIT, (action) -> {
            stateManager.setState(StateManager.State.EDITMAP);
            editMap(true);
        });

        contextManager.registerAction(ContextType.MAP, ContextManager.Action.SCROLL_UP, (action) -> {
            display.getCamera().translate(0, 1);
        });

        contextManager.registerAction(ContextType.MAP, ContextManager.Action.SCROLL_DOWN, (action) -> {
            display.getCamera().translate(0, -1);
        });

        contextManager.registerAction(ContextType.MAP, ContextManager.Action.SCROLL_LEFT, (action) -> {
            display.getCamera().translate(-1, 0);
        });

        contextManager.registerAction(ContextType.MAP, ContextManager.Action.SCROLL_RIGHT, (action) -> {
            display.getCamera().translate(1, 0);
        });

        contextManager.registerAction(ContextType.MAP, ContextManager.Action.ZOOM_IN, (action) -> {
            display.getCamera().zoom -= 0.1f;
        });

        contextManager.registerAction(ContextType.MAP, ContextManager.Action.ZOOM_OUT, (action) -> {
            display.getCamera().zoom += 0.1f;
        });

        contextManager.registerAction(ContextType.EDITMAP, ContextManager.Action.DIG, (action) -> {
            digMap();
        });

        contextManager.registerAction(ContextType.EDITMAP, ContextManager.Action.FILL, (action) -> {
            fillMap();
        });

        contextManager.registerAction(ContextType.EDITMAP, ContextManager.Action.TARGET, (action) -> {
            setTarget();
        });

        contextManager.registerAction(ContextType.EDITMAP, ContextManager.Action.TOGGLE_VIEW, (action) -> {
            stateManager.setState(StateManager.State.CRAWL);
            editMap(false);
        });

        contextManager.registerAction(ContextType.EDITMAP, ContextManager.Action.TOGGLE_EDIT, (action) -> {
            stateManager.setState(StateManager.State.MAP);
            editMap(false);
        });
    }

    private void dumpEntities() {
        int i = 0;
        for (Entity entity : getEngine().getEntities()) {
            System.out.println("=== Entity #" + i + " ===");
            for (Component component : entity.getComponents()) {
                System.out.println(" * " + component.getClass().getSimpleName());
            }
            i++;
        }
    }

    private void digMap() {
        int x = 0;
        int y = 0;
        for (Entity entity : getEntities()) {
            Controller controller = cm.get(entity);
            Position pos = pm.get(entity);

            if (controller != null && controller.isActive() && pos != null) {
                x = pos.getX();
                y = pos.getY();
                break;
            }
        }

        System.out.println("Dig at " + x + "," + y);
        mapLayer.setCell(x, y, LayerCellType.FLOOR, false);
        mapLayer.setDirty(true);
    }

    private void fillMap() {
        int x = 0;
        int y = 0;
        for (Entity entity : getEntities()) {
            Controller controller = cm.get(entity);
            Position pos = pm.get(entity);

            if (controller != null && controller.isActive() && pos != null) {
                x = pos.getX();
                y = pos.getY();
                break;
            }
        }

        System.out.println("Fill " + x + "," + y);
        mapLayer.setCell(x, y, LayerCellType.WALL, true);
        mapLayer.setDirty(true);
    }

    private void editMap(boolean edit) {
        for (Entity entity : getEntities()) {
            Controller controller = cm.get(entity);
            Hidden hidden = hm.get(entity);

            if (controller == null) {
                continue;
            }

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

    private void setTarget() {
        int x = 0;
        int y = 0;
        for (Entity entity : getEntities()) {
            Controller controller = cm.get(entity);
            Position pos = pm.get(entity);

            if (controller != null && controller.isActive() && pos != null) {
                x = pos.getX();
                y = pos.getY();
                System.out.println("Target");
                break;
            }
        }

        for (Entity entity : getEntities()) {
            AI ai = am.get(entity);

            if (ai != null) {
                entity.add(new Goal(x, y));
                break;
            }
        }
    }

    @Override
    public void dispose() {
    }
}
