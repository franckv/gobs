package com.gobs.ui.gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.gobs.ui.GUI;
import com.gobs.ui.GUI.GUIElement;
import com.gobs.ui.GUIStyle;

public class GdxGUIStylist implements Disposable {
    private ObjectMap<String, BitmapFont> fonts;
    public ObjectMap<String, GUIStyle<Color, BitmapFont>> styles;

    public GUIStyle<Color, BitmapFont> currentStyle;

    public GdxGUIStylist() {
        styles = new ObjectMap<>();
        fonts = new ObjectMap<>();

        currentStyle = new GUIStyle<>();

        for (GUIElement e : GUIElement.values()) {
            currentStyle.setFontColor(e, Color.WHITE);
            switch (e) {
                case BUTTON:
                    currentStyle.setFontBgColor(e, Color.GRAY);
                    break;
                case BUTTON_SELECTED:
                    currentStyle.setFontBgColor(e, Color.LIGHT_GRAY);
                    break;
                case FRAME:
                    currentStyle.setFontBgColor(e, Color.CYAN);
                    break;
                default:
                    currentStyle.setFontBgColor(e, Color.CLEAR);
                    break;
            }
        }

        styles.put("default", currentStyle);
    }

    public void addFont(String name, BitmapFont font) {
        if (fonts.size == 0) {
            for (GUIElement e : GUIElement.values()) {
                currentStyle.setFont(e, font);
            }
        }

        fonts.put(name, font);
    }

    public BitmapFont getFont(GUI.GUIElement type) {
        BitmapFont font;

        font = currentStyle.getFont(type);
        font.setColor(currentStyle.getFontColor(type));

        return font;
    }

    public BitmapFont getFont(String name) {
        BitmapFont font = currentStyle.getFont(GUI.GUIElement.LABEL);

        if (fonts.containsKey(name)) {
            font = fonts.get(name);
        }

        return font;
    }

    public Color getBackgroundColor(GUI.GUIElement type) {
        return currentStyle.getFontBgColor(type);
    }

    public GUIStyle<Color, BitmapFont> createStyle(String name) {
        return createStyle(name, currentStyle);
    }

    public GUIStyle<Color, BitmapFont> createStyle(String name, GUIStyle<Color, BitmapFont> parent) {
        GUIStyle<Color, BitmapFont> style = new GUIStyle<>(parent);
        styles.put(name, style);

        return style;
    }

    public void resetStyle() {
        currentStyle = styles.get("default");
    }

    public void selectStyle(String name) {
        if (styles.containsKey(name)) {
            currentStyle = styles.get(name);
        }
    }

    public GUIStyle<Color, BitmapFont> getCurrentStyle() {
        return currentStyle;
    }

    @Override
    public void dispose() {
        for (BitmapFont font : fonts.values()) {
            font.dispose();
        }
    }
}
