package com.gobs.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

class JsonGUILoader {
    private static class JsonFragment {
        private JsonValue value;
        private boolean enabled;

        private JsonFragment(JsonValue value) {
            this.value = value;
            this.enabled = true;
        }
    }

    private static class JsonSubstitution {
        private enum JsonSubstitutionType {
            STR, INT
        };

        private String field;
        private JsonSubstitutionType type;
        private String strValue;
        private int intValue;

        private JsonSubstitution(String field, String value) {
            this.field = field;
            this.type = JsonSubstitutionType.STR;
            this.strValue = value;
        }

        private JsonSubstitution(String field, int value) {
            this.field = field;
            this.type = JsonSubstitutionType.INT;
            this.intValue = value;
        }
    }

    private GUI gui;
    private JsonValue root;
    private ObjectMap<String, JsonFragment> fragments;
    private ObjectMap<String, JsonSubstitution> substitutions;
    private int idx;

    JsonGUILoader(GUI gui) {
        this.gui = gui;

        fragments = new ObjectMap<>();
        substitutions = new ObjectMap<>();

        idx = 0;
    }

    void load(Reader file) {
        JsonReader reader = new JsonReader();

        root = reader.parse(file);

        for (JsonValue value : root) {
            if (value.has("fragment") && value.has("content")) {
                String fragmentName = value.getString("fragment");
                JsonFragment fragment = new JsonFragment(value.get("content"));
                if (value.has("enabled") && value.getString("enabled").equalsIgnoreCase("false")) {
                    fragment.enabled = false;
                }
                fragments.put(fragmentName, fragment);
            }
        }
    }

    void showFragment(String fragment) {
        if (fragments.containsKey(fragment)
                && fragments.get(fragment).enabled) {
            parse(fragments.get(fragment).value);
        }
    }

    void enableFragment(String fragment, boolean enabled) {
        if (fragments.containsKey(fragment)) {
            fragments.get(fragment).enabled = enabled;
        }
    }

    void setStringValue(String id, String field, String value) {
        substitutions.put(id, new JsonSubstitution(field, value));
    }

    void setIntValue(String id, String field, int value) {
        substitutions.put(id, new JsonSubstitution(field, value));
    }

    private void parse(JsonValue value) {
        if (value == null) {
            return;
        }

        if (value.isArray()) {
            for (JsonValue child : value) {
                parse(child);
            }
        } else if (value.has("type")) {
            switch (value.getString("type")) {
                case "layout":
                    parseLayout(value);
                    break;
                case "label":
                    parseLabel(value);
                    break;
                case "frame":
                    parseFrame(value);
                    break;
                case "box":
                    parseBox(value);
                    break;
                case "pusher":
                    parsePusher(value);
                    break;
                case "repeater":
                    parseRepeater(value);
                    break;
                case "spacer":
                    parseSpacer(value);
                    break;
                case "font":
                    parseFont(value);
                    break;
                case "reference":
                    parseReference(value);
                    break;
            }
        }
    }

    private void parseLayout(JsonValue value) {
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
                parse(child);
            }
        }

        gui.endSection();
    }

    private void parseLabel(JsonValue value) {
        String label = getString(value, "label");

        if (label != null) {
            gui.Label(label);
        }
    }

    private void parseFrame(JsonValue value) {
        int width = value.getInt("width");
        int height = value.getInt("height");

        gui.Frame(width, height);
    }

    private void parseBox(JsonValue value) {
        String id = getString(value, "id");
        int width = value.getInt("width");
        int height = value.getInt("height");

        gui.Box(id, width, height);
    }

    private void parsePusher(JsonValue value) {
        if (value.has("id")) {
            JsonSubstitution sub = substitutions.get(value.getString("id"));
            gui.pushToEnd(sub.strValue);
        } else if (value.has("value")) {
            gui.pushToEnd(value.getFloat("value"));
        }
    }

    private void parseSpacer(JsonValue value) {
        gui.Spacer(getInt(value, "value"));
    }

    private void parseRepeater(JsonValue value) {
        int count = value.getInt("count");

        for (idx = 0; idx < count; idx++) {
            parse(value.get("content"));
        }
    }

    private void parseFont(JsonValue value) {
        String font = getString(value, "font");
        if (font != null) {
            gui.setFont(value.getString("font"));
        }

        String colorName = getString(value, "color");
        if (colorName != null) {
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

    private void parseReference(JsonValue value) {
        String id = value.getString("id");

        showFragment(id);
    }

    private String getString(JsonValue value, String name) {
        if (value.has("id")) {
            String id = value.getString("id").replace("${i}", Integer.toString(idx));
            if (substitutions.containsKey(id) && substitutions.get(id).field.equals(name)) {
                return substitutions.get(id).strValue;
            }
        }

        if (value.has(name)) {
            return value.getString(name).replace("${i}", Integer.toString(idx));
        }

        return null;
    }

    private int getInt(JsonValue value, String name) {
        if (value.has("id")) {
            String id = value.getString("id").replace("${i}", Integer.toString(idx));
            if (substitutions.containsKey(id) && substitutions.get(id).field.equals(name)) {
                return substitutions.get(id).intValue;
            }
        }

        return value.getInt(name);
    }
}
