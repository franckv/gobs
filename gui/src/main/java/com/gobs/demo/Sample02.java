package com.gobs.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Sample02 extends DemoApplication {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1024;
        config.height = 768;
        config.resizable = false;
        config.foregroundFPS = 75;
        new LwjglApplication(new Sample02(), config);
    }

    @Override
    public void create() {
        super.create();

        gui.addFont("font", generateFont("sazanami-mincho.ttf", 16));

        guiLoader.load(Gdx.files.internal("portraits.json").reader());

        int nPlayers = 3;
        int boxW = 250;
        int margin = 20;
        int spacing = 30;

        float size = nPlayers * boxW + (nPlayers - 1) * spacing;
        float space = (viewport.getWorldWidth() - size - 2 * margin) / 2 - spacing;

        guiLoader.setIntValue("charactersSpacing", "width", (int) space);
    }

    @Override
    public void render() {
        super.render();

        batch.begin();

        gui.begin();

        guiLoader.showFragment("ui");

        batch.end();

        gui.end();

        gui.showCenters(16);
    }
}
