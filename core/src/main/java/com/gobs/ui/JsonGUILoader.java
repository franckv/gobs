package com.gobs.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class JsonGUILoader {
    private GUI gui;
    private JsonValue root;
    private Map<String, JsonValue> fragments;
    
    public JsonGUILoader(GUI gui) {
        this.gui = gui;
        
        fragments = new HashMap<>();
    }
    
    public void load(Reader file) {
        JsonReader reader = new JsonReader();
        
        root = reader.parse(file);
        
        for (JsonValue value : root) {
            if (value.has("fragment") && value.has("content")) {
                String fragment = value.getString("fragment");
                fragments.put(fragment, value.get("content"));
            }
        }
    }
    
    public void showFragment(String fragment, Map<String, String> resolver) {
        if (fragments.containsKey(fragment)) {
            parse(fragments.get(fragment), resolver);
        }
    }
    
    private void parse(JsonValue value, Map<String, String> resolver) {
        if (value == null) {
            return;
        }
        
        if (value.isArray()) {
            for (JsonValue child : value) {
                parse(child, resolver);
            }
        } else if (value.has("type")) {
            switch (value.getString("type")) {
                case "layout":
                    parseLayout(value, resolver);
                    break;
                case "label":
                    parseLabel(value, resolver);
                    break;
                case "frame":
                    parseFrame(value);
                    break;
                case "box":
                    parseBox(value, resolver);
                    break;
                case "pusher":
                    parsePusher(value, resolver);
                    break;
                case "repeater":
                    parseRepeater(value, resolver);
                case "spacer":
                    parseSpacer(value, resolver);
                case "font":
                    parseFont(value, resolver);
                    break;
            }
        }
    }
    
    private void parseLayout(JsonValue value, Map<String, String> resolver) {
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
                parse(child, resolver);
            }
        }
        
        gui.endSection();
    }
    
    private void parseLabel(JsonValue value, Map<String, String> resolver) {
        String label = resolve(value, "label", resolver);
        
        gui.Label(label);
    }
    
    private void parseFrame(JsonValue value) {
        int width = value.getInt("width");
        int height = value.getInt("height");
        
        gui.Frame(width, height);
    }
    
    private void parseBox(JsonValue value, Map<String, String> resolver) {
        String id = resolve(value, "id", resolver);
        int width = value.getInt("width");
        int height = value.getInt("height");
        
        gui.Box(id, width, height);
    }
    
    private void parsePusher(JsonValue value, Map<String, String> resolver) {
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
    
    private void parseSpacer(JsonValue value, Map<String, String> resolver) {
        if (value.has("value")) {
            gui.Spacer(value.getFloat("value"));
        } else if (value.has("valueStr")) {
            gui.Spacer(Integer.parseInt(resolve(value, "valueStr", resolver)));
        }
    }
    
    private void parseRepeater(JsonValue value, Map<String, String> resolver) {
        int count = value.getInt("count");
        
        for (int i = 0; i < count; i++) {
            resolver.put("${i}", Integer.toString(i));
            parse(value.get("content"), resolver);
        }
    }
    
    private void parseFont(JsonValue value, Map<String, String> resolver) {
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
    
    private String resolve(JsonValue value, String name, Map<String, String> resolver) {
        String label = value.getString(name);

        // loop counter may be embeded in variable name: do 2 pass resolution
        if (resolver.containsKey("${i}")) {
            label = label.replace("${i}", resolver.get("${i}"));
        }
        
        for (Map.Entry<String, String> entry : resolver.entrySet()) {
            label = label.replace(entry.getKey(), entry.getValue());
        }
        
        return label;
    }
}
