package com.gobs.demo;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.gobs.ui.GUI;
import com.gobs.ui.GUILayout;
import com.gobs.ui.GUIStyle;

public class Sample01 extends DemoApplication {
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

        gui.addFont("small", generateFont("sazanami-mincho.ttf", 16));
        gui.addFont("medium", generateFont("sazanami-mincho.ttf", 24));
        gui.addFont("large", generateFont("sazanami-mincho.ttf", 32));

        GUIStyle<Color, BitmapFont> style = gui.createStyle("red");
        style.setFontColor(GUI.GUIElement.LABEL, Color.RED);
        style.setFont(GUI.GUIElement.LABEL, gui.getFont("medium"));
    }

    @Override
    public void render() {
        super.render();

        batch.begin();

        gui.begin();

        gui.setMargin(13, 13);
        gui.setSpacing(13);

        gui.createSection("screen", GUILayout.FlowDirection.HORIZONTAL);
        {
            gui.createSection("panel1", GUILayout.FlowDirection.VERTICAL);
            {
                gui.Label("A simple label");

                gui.createSection("multiline", GUILayout.FlowDirection.HORIZONTAL);
                {
                    gui.Label("One label");
                    gui.selectStyle("red");
                    gui.Label("Two labels");
                }
                gui.endSection();

                gui.resetStyle();

                gui.Label("A spacer");

                gui.createSection("spacer", GUILayout.FlowDirection.NONE);
                {
                    gui.Spacer(273, 299);
                }
                gui.endSection();

                gui.Label("A table");

                for (int i = 0; i < 4; i++) {
                    gui.createSection("line " + i, GUILayout.FlowDirection.HORIZONTAL);

                    for (int j = 0; j < 4; j++) {
                        gui.createSection("line " + i, GUILayout.FlowDirection.NONE);
                        gui.Label("Cell " + i + "" + j);
                        gui.endSection();
                    }

                    gui.endSection();
                }
            }
            gui.endSection();

            gui.createSection("panel2", GUILayout.FlowDirection.VERTICAL);
            {
                gui.Label("Imbricated layouts");

                gui.createSection("Line1", GUILayout.FlowDirection.HORIZONTAL);
                {
                    gui.createSection("Column11", GUILayout.FlowDirection.VERTICAL);
                    {
                        gui.createSection("Line111", GUILayout.FlowDirection.HORIZONTAL);
                        {
                            gui.createSection("Column1111", GUILayout.FlowDirection.VERTICAL);
                            gui.Label("AAA");
                            gui.Label("AAA");
                            gui.Label("AAAAAA");
                            gui.Label("AAA");
                            gui.endSection();

                            gui.createSection("Column1112", GUILayout.FlowDirection.VERTICAL);
                            gui.Label("BBB");
                            gui.Label("BB");
                            gui.Label("BBB");
                            gui.Label("BBB");
                            gui.endSection();
                        }
                        gui.endSection();

                        gui.createSection("Line112", GUILayout.FlowDirection.HORIZONTAL);
                        gui.Label("CCC");
                        gui.endSection();

                        gui.createSection("Line113", GUILayout.FlowDirection.HORIZONTAL);
                        gui.Label("DDD");
                        gui.Label("DDD");
                        gui.Label("DDD");
                        gui.Label("DDD");
                        gui.endSection();
                    }
                    gui.endSection(); // Column11

                    gui.createSection("Column12", GUILayout.FlowDirection.VERTICAL);
                    gui.Label("EEE");
                    gui.Label("EEE");
                    gui.Label("EEE");
                    gui.Label("EEE");
                    gui.Label("EEE");
                    gui.Label("EEE");
                    gui.Label("EEE");
                    gui.Label("EEE");
                    gui.Label("EEE");
                    gui.Label("EEE");
                    gui.endSection();
                }
                gui.endSection(); // Line1
            }
        }
        gui.endSection();

        batch.end();

        gui.end();

        gui.showRuler(13);
    }
}
