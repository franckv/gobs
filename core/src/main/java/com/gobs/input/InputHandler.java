package com.gobs.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.ObjectMap;
import com.gobs.ui.InputMap.Input;
import com.gobs.ui.InputMap;

public class InputHandler extends InputAdapter {
    InputMap inputMap;

    ObjectMap<Integer, Input> codes;

    public InputHandler(int keyRepeat, int keyDelay) {
        inputMap = new InputMap(keyRepeat, keyDelay);

        codes = new ObjectMap<>();

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
        codes.put(Keys.B, Input.B);
        codes.put(Keys.C, Input.C);
        codes.put(Keys.D, Input.D);
        codes.put(Keys.E, Input.E);
        codes.put(Keys.F, Input.F);
        codes.put(Keys.G, Input.G);
        codes.put(Keys.H, Input.H);
        codes.put(Keys.I, Input.I);
        codes.put(Keys.J, Input.J);
        codes.put(Keys.K, Input.K);
        codes.put(Keys.L, Input.L);
        codes.put(Keys.M, Input.M);
        codes.put(Keys.N, Input.N);
        codes.put(Keys.O, Input.O);
        codes.put(Keys.P, Input.P);
        codes.put(Keys.Q, Input.Q);
        codes.put(Keys.R, Input.R);
        codes.put(Keys.S, Input.S);
        codes.put(Keys.T, Input.T);
        codes.put(Keys.U, Input.U);
        codes.put(Keys.V, Input.V);
        codes.put(Keys.W, Input.W);
        codes.put(Keys.X, Input.X);
        codes.put(Keys.Y, Input.Y);
        codes.put(Keys.Z, Input.Z);
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean result = true;

        if (codes.containsKey(keycode)) {
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
}
