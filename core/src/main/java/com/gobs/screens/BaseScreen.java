package com.gobs.screens;

import com.badlogic.gdx.Screen;
import com.gobs.GameState;

/**
 *
 */
public class BaseScreen implements Screen {
    public BaseScreen() {

    }
    
    @Override
    public void show() {
    }
    
    @Override
    public void render(float delta) {
        GameState.getEngine().update(delta);
    }
    
    @Override
    public void resize(int width, int height) {
        GameState.getMapViewport().update(width / GameState.getTileSize(), height / GameState.getTileSize());
        GameState.getOverlayViewport().update(width, height);
        GameState.getFPVViewport().update(width, height);
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
