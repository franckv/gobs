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
import com.gobs.input.ContextManager.ContextType;
import com.gobs.input.InputHandler;
import com.gobs.map.Layer;
import com.gobs.map.LayerCell.LayerCellType;
import com.gobs.ui.GUI;
import com.gobs.ui.Input;
import com.gobs.ui.InputMap;

public class InputSystem extends EntityProcessingSystem {
    private MapDisplay display;
    private InputHandler inputHandler;
    private ContextManager contextManager;
    private StateManager stateManager;
    private Layer mapLayer;
    
    int repeat = 0;
    int repeatWait = 20;
    int rate = 1;

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

        mapInputs();
        registerActions();

        contextManager.activateContext(ContextType.CRAWLING);
        contextManager.activateContext(ContextType.GLOBAL);
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
            }

            repeat += rate;
        } else {
            repeat = 0;
            rate = 1;
        }
    }

    private void mapInputs() {
        contextManager.mapInput(ContextType.GLOBAL, Input.ESCAPE, ContextManager.Action.EXIT);
        contextManager.mapInput(ContextType.GLOBAL, Input.E, ContextManager.Action.DUMP);

        contextManager.mapInput(ContextType.CRAWLING, Input.LEFT, ContextManager.Action.MOVE_LEFT);
        contextManager.mapInput(ContextType.CRAWLING, Input.RIGHT, ContextManager.Action.MOVE_RIGHT);
        contextManager.mapInput(ContextType.CRAWLING, Input.UP, ContextManager.Action.MOVE_UP);
        contextManager.mapInput(ContextType.CRAWLING, Input.DOWN, ContextManager.Action.MOVE_DOWN);
        contextManager.mapInput(ContextType.CRAWLING, Input.TAB, ContextManager.Action.TOGGLE_VIEW);
        contextManager.mapInput(ContextType.CRAWLING, Input.E, ContextManager.Action.DUMP);

        contextManager.mapInput(ContextType.MAP, Input.Q, ContextManager.Action.SCROLL_LEFT);
        contextManager.mapInput(ContextType.MAP, Input.D, ContextManager.Action.SCROLL_RIGHT);
        contextManager.mapInput(ContextType.MAP, Input.Z, ContextManager.Action.SCROLL_UP);
        contextManager.mapInput(ContextType.MAP, Input.S, ContextManager.Action.SCROLL_DOWN);
        contextManager.mapInput(ContextType.MAP, Input.A, ContextManager.Action.ZOOM_IN);
        contextManager.mapInput(ContextType.MAP, Input.W, ContextManager.Action.ZOOM_OUT);
        contextManager.mapInput(ContextType.MAP, Input.ESCAPE, ContextManager.Action.EXIT);
        contextManager.mapInput(ContextType.MAP, Input.TAB, ContextManager.Action.TOGGLE_VIEW);
        contextManager.mapInput(ContextType.MAP, Input.SPACE, ContextManager.Action.TOGGLE_EDIT);

        contextManager.mapInput(ContextType.EDITMAP, Input.D, ContextManager.Action.DIG);
        contextManager.mapInput(ContextType.EDITMAP, Input.F, ContextManager.Action.FILL);
        contextManager.mapInput(ContextType.EDITMAP, Input.ENTER, ContextManager.Action.TARGET);
        contextManager.mapInput(ContextType.EDITMAP, Input.SPACE, ContextManager.Action.TOGGLE_EDIT);
        contextManager.mapInput(ContextType.EDITMAP, Input.LEFT, ContextManager.Action.MOVE_LEFT);
        contextManager.mapInput(ContextType.EDITMAP, Input.RIGHT, ContextManager.Action.MOVE_RIGHT);
        contextManager.mapInput(ContextType.EDITMAP, Input.UP, ContextManager.Action.MOVE_UP);
        contextManager.mapInput(ContextType.EDITMAP, Input.DOWN, ContextManager.Action.MOVE_DOWN);
        contextManager.mapInput(ContextType.EDITMAP, Input.TAB, ContextManager.Action.TOGGLE_VIEW);
    }

    private void registerActions() {
        contextManager.registerAction(ContextType.GLOBAL, ContextManager.Action.EXIT, (action) -> {
            Gdx.app.exit();
        });

        contextManager.registerAction(ContextType.GLOBAL, ContextManager.Action.DUMP, (action) -> {
            dumpEntities();
        });

        contextManager.registerAction(ContextType.CRAWLING, ContextManager.Action.MOVE_UP, (action) -> {
            setCommand(CommandType.UP);
        });

        contextManager.registerAction(ContextType.CRAWLING, ContextManager.Action.MOVE_DOWN, (action) -> {
            setCommand(CommandType.DOWN);
        });

        contextManager.registerAction(ContextType.CRAWLING, ContextManager.Action.MOVE_LEFT, (action) -> {
            setCommand(CommandType.LEFT);
        });

        contextManager.registerAction(ContextType.CRAWLING, ContextManager.Action.MOVE_RIGHT, (action) -> {
            setCommand(CommandType.RIGHT);
        });

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

        contextManager.registerAction(ContextType.EDITMAP, ContextManager.Action.MOVE_UP, (action) -> {
            setCommand(CommandType.UP);
        });

        contextManager.registerAction(ContextType.EDITMAP, ContextManager.Action.MOVE_DOWN, (action) -> {
            setCommand(CommandType.DOWN);
        });

        contextManager.registerAction(ContextType.EDITMAP, ContextManager.Action.MOVE_LEFT, (action) -> {
            setCommand(CommandType.LEFT);
        });

        contextManager.registerAction(ContextType.EDITMAP, ContextManager.Action.MOVE_RIGHT, (action) -> {
            setCommand(CommandType.RIGHT);
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

    private boolean setCommand(CommandType type) {
        Command command = new Command(type);
        for (Entity entity : getEntities()) {
            Controller controller = cm.get(entity);

            if (controller != null && controller.isActive()) {
                entity.add(command);
                return true;
            }
        }

        return false;
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
