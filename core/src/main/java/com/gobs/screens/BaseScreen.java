package com.gobs.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Screen;
import com.gobs.display.DisplayManager;

/**
 *
 */
public class BaseScreen implements Screen {
    private DisplayManager displayManager;
    private Engine engine;
    
    public BaseScreen(DisplayManager displayManager, Engine engine) {
        this.displayManager = displayManager;
        this.engine = engine;
    }
    
    @Override
    public void show() {
    }
    
    @Override
    public void render(float delta) {
        engine.update(delta);
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
