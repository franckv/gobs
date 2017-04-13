package com.gobs.demo;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gobs.ui.GUILayout;

public class Sample01 extends DemoGUI {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1024;
        config.height = 768;
        config.resizable = false;
        config.foregroundFPS = 75;
        new LwjglApplication(new Sample01(), config);
    }

    @Override
    public void create() {
        super.create();

        addFont("font", getFont("sazanami-mincho.ttf", 16));

        setShowLayouts(true);
    }

    @Override
    public void render() {
        begin();

        createSection("table", GUILayout.FlowDirection.VERTICAL);

        setMargin(13, 13);
        setSpacing(13);

        Label("A simple label");

        createSection("multiline", GUILayout.FlowDirection.VERTICAL);

        Label("Test");

        endSection();

        Label("Another label");

        for (int i = 0; i < 4; i++) {
            createSection("line " + i, GUILayout.FlowDirection.HORIZONTAL);

            for (int j = 0; j < 4; j++) {
                Label("Cell " + i + "" + j);
            }

            endSection();
        }

        endSection();

        end();

        showRuler(13);
    }
}
