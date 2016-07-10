package com.gobs;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class GobsGame extends Game {
    @Override
    public void create() {
        GameState.getGameState().setGame(this);
        setScreen(GameState.getGameState().getScreen());
        Gdx.input.setInputProcessor(GameState.getInputHandler());

        Gdx.app.setLogLevel(Application.LOG_INFO);
    }

    @Override
    public void dispose() {
        super.dispose();

        GameState.getGameState().dispose();
    }
}
