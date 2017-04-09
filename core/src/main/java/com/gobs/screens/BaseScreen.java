package com.gobs.screens;

import com.artemis.World;
import com.badlogic.gdx.Screen;
import com.gobs.display.DisplayManager;

public class BaseScreen implements Screen {
    private DisplayManager displayManager;
    private World world;

    public BaseScreen(DisplayManager displayManager, World world) {
        this.displayManager = displayManager;
        this.world = world;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        world.setDelta(delta);
        world.process();
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
