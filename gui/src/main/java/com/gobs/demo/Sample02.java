package com.gobs.demo;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gobs.ui.GUILayout;

public class Sample02 extends DemoGUI {
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

        addFont("font", getFont("sazanami-mincho.ttf", 16));

        setShowLayouts(true);
    }

    @Override
    public void render() {
        begin();

        setMargin(13, 13);
        setSpacing(13);

        setFont("small");

        createSection("Line1", GUILayout.FlowDirection.HORIZONTAL);
        {
            createSection("Column11", GUILayout.FlowDirection.VERTICAL);
            {
                createSection("Line111", GUILayout.FlowDirection.HORIZONTAL);
                {
                    createSection("Column1111", GUILayout.FlowDirection.VERTICAL);
                    Label("AAA");
                    Label("AAA");
                    Label("AAAAAA");
                    Label("AAA");
                    endSection();

                    createSection("Column1112", GUILayout.FlowDirection.VERTICAL);
                    Label("BBB");
                    Label("BB");
                    Label("BBB");
                    Label("BBB");
                    endSection();
                }
                endSection();

                createSection("Line112", GUILayout.FlowDirection.HORIZONTAL);
                Label("CCC");
                endSection();

                createSection("Line113", GUILayout.FlowDirection.HORIZONTAL);
                Label("DDD");
                Label("DDD");
                Label("DDD");
                Label("DDD");
                endSection();
            }
            endSection(); // Column11

            createSection("Column12", GUILayout.FlowDirection.VERTICAL);
            Label("EEE");
            Label("EEE");
            Label("EEE");
            Label("EEE");
            Label("EEE");
            Label("EEE");
            Label("EEE");
            Label("EEE");
            Label("EEE");
            Label("EEE");
            endSection();
        }
        endSection(); // Line1

        end();

        showRuler(13);
    }
}
