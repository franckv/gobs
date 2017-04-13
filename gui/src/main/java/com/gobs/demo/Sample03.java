package com.gobs.demo;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.gobs.ui.GUILayout;

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

        gui.addFont("font", getFont("sazanami-mincho.ttf", 16));
    }

    @Override
    public void render() {
        super.render();

        batch.begin();

        gui.begin();

        gui.createSection("screen", GUILayout.FlowDirection.VERTICAL);
        {
            gui.setMargin(16);
            gui.setSpacing(32);

            gui.createSection("boxes", GUILayout.FlowDirection.HORIZONTAL);

            if (gui.Box("box1", 64, 64)) {
                System.out.println("Box 1 clicked");
            }

            if (gui.Box("box2", 64, 64)) {
                System.out.println("Box 2 clicked");
            }

            gui.endSection();

            gui.createSection("buttons", GUILayout.FlowDirection.HORIZONTAL);
            gui.setSpacing(16);

            gui.setFontColor(Color.BLUE);
            
            if (gui.Button("button1", 128, 64, "Button 1")) {
                System.out.println("Button 1 clicked");
            }

            if (gui.Button("button2", 128, 64, "Button 2")) {
                System.out.println("Button 2 clicked");
            }

            gui.endSection();
        }
        gui.endSection();

        batch.end();

        gui.end();
        
        gui.showRuler(16);
    }
}
