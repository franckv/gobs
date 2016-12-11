package com.gobs.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.gobs.GameState;
import com.gobs.RunningState;
import com.gobs.map.LayerCell.LayerCellType;
import com.gobs.components.AI;
import com.gobs.components.Command;
import com.gobs.components.Command.CommandType;
import com.gobs.components.Controller;
import com.gobs.components.Goal;
import com.gobs.components.Hidden;
import com.gobs.components.Position;
import com.gobs.input.ContextManager;
import com.gobs.input.ContextManager.ContextType;
import com.gobs.ui.Input;
import com.gobs.input.InputHandler;
import com.gobs.ui.InputMap;
import com.gobs.ui.GUI;

public class InputSystem extends EntityProcessingSystem {
    private InputHandler inputHandler;
    private StateMachine<GameState, RunningState> stateMachine;
    private ContextManager contextManager;

    int repeat = 0;
    int repeatWait = 20;
    int rate = 1;

    private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
    private ComponentMapper<Controller> cm = ComponentMapper.getFor(Controller.class);
    private ComponentMapper<Hidden> hm = ComponentMapper.getFor(Hidden.class);
    private ComponentMapper<AI> am = ComponentMapper.getFor(AI.class);

    public InputSystem() {
        this(0);
    }

    public InputSystem(int priority) {
        super(Family.one(Controller.class, AI.class).get(), priority);

        inputHandler = GameState.getInputHandler();
        stateMachine = new DefaultStateMachine<>(GameState.getGameState(), RunningState.CRAWL);

        repeatWait = GameState.getConfig().getRepeat();

        contextManager = GameState.getContextManager();

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
            stateMachine.changeState(RunningState.MAP);
            GameState.getMapLayer().setDirty(true);
        });

        contextManager.registerAction(ContextType.MAP, ContextManager.Action.TOGGLE_VIEW, (action) -> {
            stateMachine.changeState(RunningState.CRAWL);
        });

        contextManager.registerAction(ContextType.MAP, ContextManager.Action.TOGGLE_EDIT, (action) -> {
            stateMachine.changeState(RunningState.EDITMAP);
            editMap(true);
        });

        contextManager.registerAction(ContextType.MAP, ContextManager.Action.SCROLL_UP, (action) -> {
            GameState.getMapCamera().translate(0, 1);
        });

        contextManager.registerAction(ContextType.MAP, ContextManager.Action.SCROLL_DOWN, (action) -> {
            GameState.getMapCamera().translate(0, -1);
        });

        contextManager.registerAction(ContextType.MAP, ContextManager.Action.SCROLL_LEFT, (action) -> {
            GameState.getMapCamera().translate(-1, 0);
        });

        contextManager.registerAction(ContextType.MAP, ContextManager.Action.SCROLL_RIGHT, (action) -> {
            GameState.getMapCamera().translate(1, 0);
        });

        contextManager.registerAction(ContextType.MAP, ContextManager.Action.ZOOM_IN, (action) -> {
            GameState.getMapCamera().zoom -= 0.1f;
        });

        contextManager.registerAction(ContextType.MAP, ContextManager.Action.ZOOM_OUT, (action) -> {
            GameState.getMapCamera().zoom += 0.1f;
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
            stateMachine.changeState(RunningState.CRAWL);
            editMap(false);
        });

        contextManager.registerAction(ContextType.EDITMAP, ContextManager.Action.TOGGLE_EDIT, (action) -> {
            stateMachine.changeState(RunningState.MAP);
            editMap(false);
        });
    }

    private boolean setCommand(CommandType type) {
        Command command = new Command(type);
        for (Entity entity : getEntities()) {
            Controller controller = cm.get(entity);

            if (controller != null && controller.getState() == stateMachine.getCurrentState()) {
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

            if (controller != null && pos != null) {
                x = pos.getX();
                y = pos.getY();
                break;
            }
        }

        System.out.println("Dig at " + x + "," + y);
        GameState.getMapLayer().setCell(x, y, LayerCellType.FLOOR, false);

    }

    private void fillMap() {
        int x = 0;
        int y = 0;
        for (Entity entity : getEntities()) {
            Controller controller = cm.get(entity);
            Position pos = pm.get(entity);

            if (controller != null && pos != null) {
                x = pos.getX();
                y = pos.getY();
                break;
            }
        }

        System.out.println("Fill " + x + "," + y);
        GameState.getMapLayer().setCell(x, y, LayerCellType.WALL, true);

    }

    private void editMap(boolean edit) {
        for (Entity entity : getEntities()) {
            Controller controller = cm.get(entity);
            Hidden hidden = hm.get(entity);

            // show cursor only in select mode
            if (edit && controller != null && controller.getState() == RunningState.EDITMAP && hidden != null) {
                System.out.println("Show cursor");
                entity.remove(Hidden.class);
            } else if (!edit && controller != null && controller.getState() == RunningState.EDITMAP && hidden == null) {
                System.out.println("Hide cursor");
                entity.add(new Hidden());
            }
        }
    }

    private void setTarget() {
        int x = 0;
        int y = 0;
        for (Entity entity : getEntities()) {
            Controller controller = cm.get(entity);
            Position pos = pm.get(entity);

            if (controller != null && controller.getState() == RunningState.EDITMAP && pos != null) {
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
