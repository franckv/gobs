package com.gobs.demo;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gobs.ui.GUILayout;
import java.util.ArrayList;
import java.util.List;

public class Sample04 extends DemoApplication {
    List<String> values;
    int selected;

    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1024;
        config.height = 768;
        config.resizable = false;
        config.foregroundFPS = 75;
        new LwjglApplication(new Sample04(), config);
    }

    @Override
    public void create() {
        super.create();

        gui.addFont("font", getFont("sazanami-mincho.ttf", 24));

        values = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            values.add("Item " + i);
        }

        selected = -1;
    }

    @Override
    public void render() {
        super.render();

        batch.begin();

        gui.begin();

        gui.setMargin(17);
        gui.setSpacing(17);

        gui.createSection("screen", GUILayout.FlowDirection.VERTICAL);
        {
            gui.createSection("table", GUILayout.FlowDirection.NONE);
            {
                selected = gui.Table("table", "A table", values, selected);
            }
            gui.endSection();

            gui.Spacer(0, 68);
            gui.Label("Selected: " + selected);
        }
        gui.endSection();

        batch.end();

        gui.end();

        gui.showRuler(17);
    }
}
