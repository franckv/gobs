package com.gobs.demo;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gobs.ui.GUILayout;
import com.gobs.ui.GUIStyle;

public class Sample03 extends DemoApplication {
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

        gui.registerImage("palette", new TextureRegion(gui.colorsTexture));

        gui.addFont("default", generateFont("sazanami-mincho.ttf", 16));
        gui.addFont("button", generateFont("sazanami-mincho.ttf", 24));

        GUIStyle<Color, BitmapFont> style = new GUIStyle<>(gui.getStyle());

        style.getButtonFormat().setTextFont(gui.getFont("button"));
        style.getButtonSelectedFormat().setTextFont(gui.getFont("button"));

        style.getButtonFormat().setTextColor(Color.BLUE);
        style.getButtonSelectedFormat().setTextColor(Color.RED);

        style.getButtonFormat().setTextBgColor(Color.GRAY);
        style.getButtonSelectedFormat().setTextBgColor(Color.LIGHT_GRAY);

        gui.addStyle("button", style);

        style = new GUIStyle<>(style);

        style.getButtonFormat().setTextColor(Color.BLACK);

        gui.addStyle("palette", style);
    }

    @Override
    public void render() {
        super.render();

        batch.begin();

        gui.begin();

        gui.selectStyle("default");

        gui.setMargin(16);
        gui.setSpacing(16);

        gui.createSection("screen", GUILayout.FlowDirection.VERTICAL);
        {
            gui.createSection("boxes", GUILayout.FlowDirection.HORIZONTAL);
            {
                gui.createSection("", GUILayout.FlowDirection.VERTICAL);
                {
                    gui.Label("Some boxes");

                    gui.createSection("boxes", GUILayout.FlowDirection.HORIZONTAL);

                    if (gui.Box("box1", 64, 64)) {
                        System.out.println("Box 1 clicked");
                    }

                    if (gui.Box("box2", 64, 64)) {
                        System.out.println("Box 2 clicked");
                    }

                    gui.endSection();

                    gui.Label("Some buttons");

                    gui.selectStyle("button");

                    gui.createSection("buttons", GUILayout.FlowDirection.HORIZONTAL);

                    if (gui.Button("button1", 128, 64, "Button 1")) {
                        System.out.println("Button 1 clicked");
                    }

                    if (gui.Button("button2", 128, 64, "Button 2")) {
                        System.out.println("Button 2 clicked");
                    }

                    gui.endSection();
                }
                gui.endSection();

                gui.createSection("", GUILayout.FlowDirection.VERTICAL);
                {
                    gui.Label("Image");
                    gui.Image("palette", 272, 224);
                }
                gui.endSection();
            }
            gui.endSection();

            gui.selectStyle("palette");

            int x = 0;
            int y = 0;
            int step = 6;

            for (int i = 0; i < gui.palette.length; i++) {
                x = i / step;
                y = i % step;

                if (y == 0) {
                    gui.createSection("", GUILayout.FlowDirection.HORIZONTAL);
                }
                gui.getStyle().getButtonFormat().setTextBgColor(gui.palette[i]);
                gui.Button("Box" + x + ":" + y, 128, 64, gui.palette[i].toString());
                if (y == step - 1) {
                    gui.endSection();
                }
            }
            if (y != step - 1) {
                gui.endSection();
            }
        }
        gui.endSection();

        batch.end();

        gui.end();

        gui.showRuler(16);
    }
}
