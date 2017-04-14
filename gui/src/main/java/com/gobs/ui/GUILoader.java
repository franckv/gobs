package com.gobs.ui;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Load GUI from json file
 */
public abstract class GUILoader<JsonValue, Color, Font> {
    private class JsonFragment {
        private JsonValue value;
        private boolean enabled;

        private JsonFragment(JsonValue value) {
            this.value = value;
            this.enabled = true;
        }
    }

    private static class JsonSubstitution {
        private enum JsonSubstitutionType {
            STR, INT, LIST
        };

        private String field;
        private JsonSubstitutionType type;
        private String strValue;
        private int intValue;
        private Iterable<String> listValues;

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

        private JsonSubstitution(String field, Iterable<String> values) {
            this.field = field;
            this.type = JsonSubstitutionType.LIST;
            this.listValues = values;
        }
    }

    private GUI<Color, Font> gui;
    private JsonValue root;
    private Map<String, JsonFragment> fragments;
    private Map<String, JsonSubstitution> substitutions;
    private Map<String, Integer> listSelections;
    private int idx;

    public GUILoader(GUI gui) {
        this.gui = gui;

        fragments = new HashMap<>();
        substitutions = new HashMap<>();
        listSelections = new HashMap<>();

        idx = 0;
    }

    public void load(Reader file) {
        root = readFile(file);

        for (JsonValue value : readArray(root)) {
            if (hasField(value, "fragment") && hasField(value, "content")) {
                String fragmentName = readString(value, "fragment");
                JsonFragment fragment = new JsonFragment(readObject(value, "content"));
                if (hasField(value, "enabled") && !readBoolean(value, "enabled")) {
                    fragment.enabled = false;
                }
                fragments.put(fragmentName, fragment);
            }
        }
    }

    public void showFragment(String fragment) {
        if (fragments.containsKey(fragment)
                && fragments.get(fragment).enabled) {
            parse(fragments.get(fragment).value);
        }
    }

    public void enableFragment(String fragment, boolean enabled) {
        if (fragments.containsKey(fragment)) {
            fragments.get(fragment).enabled = enabled;
        }
    }

    public void toggleFragment(String fragment) {
        if (fragments.containsKey(fragment)) {
            fragments.get(fragment).enabled = !fragments.get(fragment).enabled;
        }
    }

    public void setStringValue(String id, String field, String value) {
        substitutions.put(id, new JsonSubstitution(field, value));
    }

    public void setIntValue(String id, String field, int value) {
        substitutions.put(id, new JsonSubstitution(field, value));
    }

    public void setListValue(String id, String field, Iterable<String> values) {
        substitutions.put(id, new JsonSubstitution(field, values));
    }

    public int getListSelection(String id) {
        if (listSelections.containsKey(id)) {
            return listSelections.get(id);
        }

        return -1;
    }

    private void parse(JsonValue value) {
        if (value == null) {
            return;
        }

        if (isArray(value)) {
            for (JsonValue child : readArray(value)) {
                parse(child);
            }
        } else if (hasField(value, "type")) {
            switch (readString(value, "type")) {
                case "layout":
                    parseLayout(value);
                    break;
                case "label":
                    parseLabel(value);
                    break;
                case "table":
                    parseTable(value);
                    break;
                case "frame":
                    parseFrame(value);
                    break;
                case "image":
                    parseImage(value);
                    break;
                case "imagebox":
                    parseImageBox(value);
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
                case "reference":
                    parseReference(value);
                    break;
                case "style":
                    parseStyle(value);
                    break;
            }
        }
    }

    private void parseLayout(JsonValue value) {
        String name = getString(value, "name");
        String direction = readString(value, "direction");

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

        if (hasField(value, "margin")) {
            gui.setMargin(readInt(value, "margin"));
        }

        if (hasField(value, "marginX") && hasField(value, "marginY")) {
            gui.setMargin(readInt(value, "marginX"), readInt(value, "marginY"));
        }

        if (hasField(value, "spacing")) {
            gui.setSpacing(readInt(value, "spacing"));
        }

        if (hasField(value, "children")) {
            for (JsonValue child : readChildren(value, "children")) {
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

    private void parseTable(JsonValue value) {
        String id = getString(value, "id");
        String header = getString(value, "header");
        Iterable<String> values = getList(value, "values");

        int selected = -1;

        if (listSelections.containsKey(id)) {
            selected = listSelections.get(id);
        }

        if (header != null) {
            selected = gui.Table(id, header, values, selected);
            listSelections.put(id, selected);
        }
    }

    private void parseFrame(JsonValue value) {
        int width = readInt(value, "width");
        int height = readInt(value, "height");

        gui.Frame(width, height);
    }

    private void parseImage(JsonValue value) {
        String res = readString(value, "resource");
        int width = readInt(value, "width");
        int height = readInt(value, "height");

        gui.Image(res, width, height);
    }

    private void parseImageBox(JsonValue value) {
        String id = getString(value, "id");
        String res = readString(value, "resource");
        String res_selected = readString(value, "resource_selected");
        int width = readInt(value, "width");
        int height = readInt(value, "height");

        boolean clicked = gui.ImageBox(id, res, res_selected, width, height);
    }

    private void parseBox(JsonValue value) {
        String id = getString(value, "id");
        int width = readInt(value, "width");
        int height = readInt(value, "height");

        boolean clicked = gui.Box(id, width, height);
    }

    private void parsePusher(JsonValue value) {
        if (hasField(value, "id")) {
            JsonSubstitution sub = substitutions.get(readString(value, "id"));
            // TODO: fix type
            gui.pushToEnd(sub.strValue, GUI.GUIElement.LABEL);
        } else if (hasField(value, "value")) {
            gui.pushToEnd(readFloat(value, "value"));
        }
    }

    private void parseSpacer(JsonValue value) {
        gui.Spacer(getInt(value, "width"), getInt(value, "height"));
    }

    private void parseRepeater(JsonValue value) {
        int count = readInt(value, "count");

        for (idx = 0; idx < count; idx++) {
            parse(readObject(value, "content"));
        }
    }

    private void parseReference(JsonValue value) {
        String id = readString(value, "id");

        showFragment(id);
    }

    private void parseStyle(JsonValue value) {
        String name = getString(value, "value");

        gui.selectStyle(name);
    }

    private String getString(JsonValue value, String name) {
        if (hasField(value, "id")) {
            String id = readString(value, "id").replace("${i}", Integer.toString(idx));
            if (substitutions.containsKey(id) && substitutions.get(id).field.equals(name)) {
                return substitutions.get(id).strValue;
            }
        }

        if (hasField(value, name)) {
            return readString(value, name).replace("${i}", Integer.toString(idx));
        }

        return null;
    }

    private int getInt(JsonValue value, String name) {
        if (hasField(value, "id")) {
            String id = readString(value, "id").replace("${i}", Integer.toString(idx));
            if (substitutions.containsKey(id) && substitutions.get(id).field.equals(name)) {
                return substitutions.get(id).intValue;
            }
        }

        return readInt(value, name);
    }

    private Iterable<String> getList(JsonValue value, String name) {
        if (hasField(value, "id")) {
            String id = readString(value, "id").replace("${i}", Integer.toString(idx));
            if (substitutions.containsKey(id) && substitutions.get(id).field.equals(name)) {
                return substitutions.get(id).listValues;
            }
        }

        return null;
    }

    protected abstract JsonValue readFile(Reader reader);

    protected abstract boolean hasField(JsonValue value, String field);

    protected abstract String readString(JsonValue value, String field);

    protected abstract boolean readBoolean(JsonValue value, String field);

    protected abstract int readInt(JsonValue value, String field);

    protected abstract float readFloat(JsonValue value, String field);

    protected abstract JsonValue readObject(JsonValue value, String field);

    protected abstract Iterable<JsonValue> readArray(JsonValue value);

    protected abstract Iterable<JsonValue> readChildren(JsonValue value, String field);

    protected abstract boolean isArray(JsonValue value);
}
