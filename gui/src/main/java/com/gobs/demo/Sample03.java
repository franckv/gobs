package com.gobs.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Sample03 extends DemoGUI {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1024;
        config.height = 768;
        config.resizable = false;
        config.foregroundFPS = 75;
        new LwjglApplication(new Sample03(), config);
    }

    @Override
    public void create() {
        super.create();

        addFont("font", getFont("sazanami-mincho.ttf", 16));

        load(Gdx.files.internal("portraits.json").reader());

        setShowLayouts(true);

        int nPlayers = 3;
        int boxW = 250;
        int margin = 20;
        int spacing = 30;

        float size = nPlayers * boxW + (nPlayers - 1) * spacing;
        float space = (viewport.getWorldWidth() - size - 2 * margin) / 2 - spacing;

        System.out.println(space);
        setIntValue("charactersSpacing", "width", (int) space);
    }

    @Override
    public void render() {
        super.render();

        begin();

        showFragment("ui");

        end();

        showCenters(16);
    }
}
