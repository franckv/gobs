package com.gobs.input;

import com.gobs.ui.InputMap;
import com.gobs.ui.Input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import java.util.HashMap;
import java.util.Map;

public class InputHandler extends InputAdapter {
    InputMap inputMap;
    InputMap last;

    Map<Integer, Input> codes;

    public InputHandler() {
        inputMap = new InputMap();
        last = new InputMap();
        codes = new HashMap<>();

        codes.put(Keys.LEFT, Input.LEFT);
        codes.put(Keys.RIGHT, Input.RIGHT);
        codes.put(Keys.UP, Input.UP);
        codes.put(Keys.DOWN, Input.DOWN);

        codes.put(Keys.SPACE, Input.SPACE);
        codes.put(Keys.ENTER, Input.ENTER);
        codes.put(Keys.DEL, Input.DEL);
        codes.put(Keys.ESCAPE, Input.ESCAPE);
        codes.put(Keys.TAB, Input.TAB);

        codes.put(Keys.A, Input.A);
        codes.put(Keys.D, Input.D);
        codes.put(Keys.E, Input.E);
        codes.put(Keys.M, Input.M);
        codes.put(Keys.Q, Input.Q);
        codes.put(Keys.S, Input.S);
        codes.put(Keys.T, Input.T);
        codes.put(Keys.W, Input.W);
        codes.put(Keys.Z, Input.Z);
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean result = true;

        if (codes.containsKey(keycode)) {
            last = new InputMap(inputMap);
            inputMap.set(codes.get(keycode));
        } else {
            result = false;
        }

        return result;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean result = true;

        if (codes.containsKey(keycode)) {
            last = new InputMap(inputMap);
            inputMap.clear(codes.get(keycode));
        } else {
            result = false;
        }

        return result;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        inputMap.setMouseDown(false);

        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        inputMap.setMouseDown(true);

        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        inputMap.mouseMoved(screenX, Gdx.graphics.getHeight() - screenY);

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        inputMap.mouseMoved(screenX, Gdx.graphics.getHeight() - screenY);

        return true;
    }

    public InputMap getInputMap() {
        return inputMap;
    }

    public boolean hasChanged() {
        return inputMap.equals(last);
    }

    public boolean hasInput() {
        return inputMap.hasInput();
    }
}
