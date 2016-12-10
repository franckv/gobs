package com.gobs.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
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
import com.gobs.input.Input;
import com.gobs.input.InputHandler;
import com.gobs.input.InputMap;
import com.gobs.ui.GUI;

public class InputSystem extends EntityProcessingSystem {
    private InputHandler inputHandler;
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
        this.inputHandler = GameState.getInputHandler();
        this.repeatWait = GameState.getConfig().getRepeat();
    }

    @Override
    public void update(float deltaTime) {
        InputMap inputMap = inputHandler.getInputMap();
        GUI.AcceptInput(inputMap);

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
                dispatchInput(inputMap);
            }

            repeat += rate;
        } else {
            repeat = 0;
            rate = 1;
        }
    }

    private void dispatchInput(InputMap inputMap) {
        if (acceptInputGlobal(inputMap) || acceptInputCrawl(inputMap) || acceptInputMap(inputMap) || acceptInputSelect(inputMap)) {
            return;
        }
    }

    private boolean acceptInputGlobal(InputMap inputMap) {
        if (inputMap.isPressed(Input.E)) {
            dumpEntities();
            return true;
        } else if (inputMap.isPressed(Input.ESCAPE)) {
            Gdx.app.exit();
        } else if (inputMap.isPressed(Input.TAB)) {
            GameState.toggleView();
            return true;
        } else if (inputMap.isPressed(Input.LEFT)) {
            return setCommand(CommandType.LEFT);
        } else if (inputMap.isPressed(Input.RIGHT)) {
            return setCommand(CommandType.RIGHT);
        } else if (inputMap.isPressed(Input.UP)) {
            return setCommand(CommandType.UP);
        } else if (inputMap.isPressed(Input.DOWN)) {
            return setCommand(CommandType.DOWN);
        }
        return false;
    }

    private boolean setCommand(CommandType type) {
        Command command = new Command(type);
        for (Entity entity : getEntities()) {
            Controller controller = cm.get(entity);

            if (controller != null && controller.getState() == GameState.getState()) {
                entity.add(command);
                return true;
            }
        }

        return false;
    }

    private boolean acceptInputCrawl(InputMap inputMap) {
        if (GameState.getState() != RunningState.CRAWL) {
            return false;
        }

        return false;
    }

    private boolean acceptInputMap(InputMap inputMap) {
        if (GameState.getState() != RunningState.MAP && GameState.getState() != RunningState.SELECT) {
            return false;
        }

        if (inputMap.isPressed(Input.SPACE)) {
            toggleState();
        } else if (inputMap.isPressed(Input.A)) {
            GameState.getMapCamera().zoom -= 0.1f;
        } else if (inputMap.isPressed(Input.W)) {
            GameState.getMapCamera().zoom += 0.1f;
        } else if (inputMap.isPressed(Input.Z)) {
            GameState.getMapCamera().translate(0, 1);
        } else if (inputMap.isPressed(Input.Q)) {
            GameState.getMapCamera().translate(-1, 0);
        } else if (inputMap.isPressed(Input.D)) {
            GameState.getMapCamera().translate(1, 0);
        } else if (inputMap.isPressed(Input.S)) {
            GameState.getMapCamera().translate(0, -1);
        } else {
            return false;
        }

        return true;
    }

    private boolean acceptInputSelect(InputMap inputMap) {
        if (GameState.getState() != RunningState.SELECT) {
            return false;
        }

        if (inputMap.isPressed(Input.T)) {
            digMap();
        } else if (inputMap.isPressed(Input.M)) {
            fillMap();
        } else if (inputMap.isPressed(Input.ENTER)) {
            setTarget();
        } else {
            return false;
        }

        return true;
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

            if (controller != null && pos != null
                    && controller.getState() == GameState.getState()) {
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

            if (controller != null && pos != null
                    && controller.getState() == GameState.getState()) {
                x = pos.getX();
                y = pos.getY();
                break;
            }
        }

        System.out.println("Dig at " + x + "," + y);
        GameState.getMapLayer().setCell(x, y, LayerCellType.WALL, true);

    }

    private void toggleState() {
        GameState.toggleSelect();

        for (Entity entity : getEntities()) {
            Controller controller = cm.get(entity);
            Hidden hidden = hm.get(entity);

            // show cursor only in select mode
            if (GameState.getState() != RunningState.SELECT && controller != null && hidden == null
                    && controller.getState() == RunningState.SELECT) {
                System.out.println("Hide cursor");
                entity.add(new Hidden());
            } else if (GameState.getState() == RunningState.SELECT && controller != null && hidden != null
                    && controller.getState() == GameState.getState()) {
                System.out.println("Show cursor");
                entity.remove(Hidden.class);
            }
        }
    }

    private void setTarget() {
        int x = 0;
        int y = 0;
        for (Entity entity : getEntities()) {
            Controller controller = cm.get(entity);
            Position pos = pm.get(entity);

            if (controller != null && pos != null
                    && controller.getState() == GameState.getState()) {
                x = pos.getX();
                y = pos.getY();
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
