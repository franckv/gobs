package com.gobs.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import java.io.Reader;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class JsonGUILoader {
    public static class JsonGUILoaderException extends Exception {
        JsonGUILoaderException(String message) {
            super(message);
        }
    }

    public static void load(GUI gui, Reader file, String fragment, Map<String, String> resolver) {
        JsonReader reader = new JsonReader();

        JsonValue json = reader.parse(file);

        for (JsonValue value : json) {
            if (value.has("fragment") && value.getString("fragment").equals(fragment)) {
                parse(gui, value.get("content"), resolver);
            } else {
                parse(gui, value, resolver);
            }
        }
    }

    private static void parse(GUI gui, JsonValue value, Map<String, String> resolver) {
        if (value == null) {
            return;
        }

        if (value.isArray()) {
            for (JsonValue child : value) {
                parse(gui, child, resolver);
            }
        } else if (value.has("type")) {
            switch (value.getString("type")) {
                case "layout":
                    parseLayout(gui, value, resolver);
                    break;
                case "label":
                    parseLabel(gui, value, resolver);
                    break;
                case "frame":
                    parseFrame(gui, value);
                    break;
                case "box":
                    parseBox(gui, value, resolver);
                    break;
                case "pusher":
                    parsePusher(gui, value, resolver);
                    break;
                case "repeater":
                    parseRepeater(gui, value, resolver);
                case "spacer":
                    parseSpacer(gui, value);
                case "font":
                    parseFont(gui, value, resolver);
                    break;
            }
        }
    }

    private static void parseLayout(GUI gui, JsonValue value, Map<String, String> resolver) {
        String name = value.getString("name");
        String direction = value.getString("direction");

        GUILayout.FlowDirection flowDirection = GUILayout.FlowDirection.NONE;

        switch (direction) {
            case "Horizontal":
                flowDirection = GUILayout.FlowDirection.HORIZONTAL;
                break;
            case "Vertical":
                flowDirection = GUILayout.FlowDirection.VERTICAL;
                break;
        }

        gui.createSection(name, flowDirection);

        if (value.has("margin")) {
            gui.setMargin(value.getInt("margin"));
        }

        if (value.has("marginX") && value.has("marginY")) {
            gui.setMargin(value.getInt("marginX"), value.getInt("marginY"));
        }

        if (value.has("spacing")) {
            gui.setSpacing(value.getInt("spacing"));
        }

        if (value.has("children")) {
            for (JsonValue child : value.get("children")) {
                parse(gui, child, resolver);
            }
        }

        gui.endSection();
    }

    private static void parseLabel(GUI gui, JsonValue value, Map<String, String> resolver) {
        String label = value.getString("label");

        if (resolver.containsKey(label)) {
            label = resolver.get(label);
        }

        gui.Label(label);
    }

    private static void parseFrame(GUI gui, JsonValue value) {
        int width = value.getInt("width");
        int height = value.getInt("height");

        gui.Frame(width, height);
    }

    private static void parseBox(GUI gui, JsonValue value, Map<String, String> resolver) {
        String id = resolve(value, "id", resolver);
        int width = value.getInt("width");
        int height = value.getInt("height");

        gui.Box(id, width, height);
    }

    private static void parsePusher(GUI gui, JsonValue value, Map<String, String> resolver) {
        if (value.has("value")) {
            gui.pushToEnd(value.getFloat("value"));
        } else if (value.has("height")) {
            String label = resolve(value, "height", resolver);
            gui.pushToEnd(gui.getLabelHeight(label));
        } else if (value.has("width")) {
            String label = resolve(value, "width", resolver);
            gui.pushToEnd(gui.getLabelWidth(label));
        }
    }

    private static void parseSpacer(GUI gui, JsonValue value) {
        if (value.has("value")) {
            gui.Spacer(value.getFloat("value"));
        }
    }

    private static void parseRepeater(GUI gui, JsonValue value, Map<String, String> resolver) {
        int count = value.getInt("count");

        for (int i = 0; i < count; i++) {
            resolver.put("${i}", Integer.toString(i));
            parse(gui, value.get("content"), resolver);
        }
    }

    private static void parseFont(GUI gui, JsonValue value, Map<String, String> resolver) {
        if (value.has("font")) {
            gui.setFont(value.getString("font"));
        }
        if (value.has("color")) {
            String colorName = resolve(value, "color", resolver);

            Color color = Color.GREEN;

            try {
                color = (Color) Color.class
                        .getDeclaredField(colorName.toUpperCase()).get(null);
            } catch (NoSuchFieldException | SecurityException
                    | IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(JsonGUILoader.class
                        .getName()).log(Level.SEVERE, "Invalid color " + value.getString("color"), ex);
            }
            gui.setFontColor(color);
        }
    }

    private static String resolve(JsonValue value, String name, Map<String, String> resolver) {
        String label = value.getString(name);

        for (Map.Entry<String, String> entry : resolver.entrySet()) {
            label = label.replace(entry.getKey(), entry.getValue());
        }

        return label;
    }
}
