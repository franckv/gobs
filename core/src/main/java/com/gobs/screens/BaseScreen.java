package com.gobs.screens;

import com.badlogic.gdx.Screen;
import com.gobs.GobsEngine;
import com.gobs.display.DisplayManager;

public class BaseScreen implements Screen {
    private DisplayManager displayManager;
    private GobsEngine engine;
    private double accu = 0.0;
    private float step;

    public BaseScreen(DisplayManager displayManager, GobsEngine engine, int fps) {
        this.displayManager = displayManager;
        this.engine = engine;

        this.step = 1.0f / fps;
    }

    @Override
    public void show() {
        // we render once to build the current level with dirty=true
        engine.update(0, true);
    }

    @Override
    public void render(float delta) {
        accu += delta;

        // first pass: update logic in fixed time steps (catchup if frames are skipped)
        while (accu >= step) {
            accu -= step;
            engine.update(step, false);
        }

        // second pass: render (time independant)
        engine.update(0, true);
    }

    @Override
    public void resize(int width, int height) {
        displayManager.resize(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
