package com.gobs.demo;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.gobs.ui.GUI;
import com.gobs.ui.GUILayout;
import com.gobs.ui.GUIStyle;
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

        gui.addFont("font", generateFont("sazanami-mincho.ttf", 14));
        gui.addFont("header", generateFont("sazanami-mincho.ttf", 16));

        String styleName = "sample04";

        GUIStyle<Color, BitmapFont> style = gui.createStyle(styleName);

        style.setFont(GUI.GUIElement.HEADER, gui.getFont("header"));
        style.setFontColor(GUI.GUIElement.HEADER, Color.WHITE);
        style.setFontBgColor(GUI.GUIElement.HEADER, Color.MAROON);

        style.setFontColor(GUI.GUIElement.LIST_ITEM, Color.WHITE);
        style.setFontBgColor(GUI.GUIElement.LIST_ITEM, Color.CLEAR);

        style.setFontColor(GUI.GUIElement.LIST_ITEM_SELECTED, Color.CHARTREUSE);
        style.setFontBgColor(GUI.GUIElement.LIST_ITEM_SELECTED, Color.PURPLE);

        gui.selectStyle(styleName);

        values = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            values.add("  Item " + i + "                        ");
        }

        selected = -1;
    }

    @Override
    public void render() {
        super.render();

        batch.begin();

        gui.begin();

        gui.setMargin(17);
        gui.setSpacing(15);

        gui.createSection("screen", GUILayout.FlowDirection.VERTICAL);
        {
            gui.createSection("table", GUILayout.FlowDirection.NONE);
            {
                selected = gui.Table("table", "  A table                   ", values, selected);
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
