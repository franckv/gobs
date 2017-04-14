package com.gobs.ui;

import com.gobs.ui.GUI.GUIElement;
import java.util.HashMap;
import java.util.Map;

public class GUIStyle<Color, Font> {
    public class GUIStyleAttributes {
        public Color textColor;
        public Color textBgColor;
        public Font textFont;

        public GUIStyleAttributes() {
        }

        public GUIStyleAttributes(GUIStyleAttributes parent) {
            textColor = parent.textColor;
            textBgColor = parent.textBgColor;
            textFont = parent.textFont;
        }
    }

    public GUIStyle<Color, Font> parent;

    Map<GUI.GUIElement, GUIStyleAttributes> styleAttributes;

    public GUIStyle() {
        this(null);
    }

    public GUIStyle(GUIStyle<Color, Font> parent) {
        this.parent = parent;
        styleAttributes = new HashMap<>();

        if (parent != null) {
            for (GUIElement e : GUIElement.values()) {
                styleAttributes.put(e, new GUIStyleAttributes(parent.getStyleAttributes(e)));
            }
        } else {
            for (GUIElement e : GUIElement.values()) {
                styleAttributes.put(e, new GUIStyleAttributes());
            }
        }
    }

    public GUIStyleAttributes getStyleAttributes(GUI.GUIElement type) {
        return styleAttributes.get(type);
    }

    public void setFont(GUI.GUIElement type, Font font) {
        styleAttributes.get(type).textFont = font;
    }

    public Font getFont(GUI.GUIElement type) {
        return styleAttributes.get(type).textFont;
    }

    public void setFontColor(GUI.GUIElement type, Color color) {
        styleAttributes.get(type).textColor = color;
    }

    public Color getFontColor(GUI.GUIElement type) {
        return styleAttributes.get(type).textColor;
    }

    public void setFontBgColor(GUI.GUIElement type, Color color) {
        styleAttributes.get(type).textBgColor = color;
    }

    public Color getFontBgColor(GUI.GUIElement type) {
        return styleAttributes.get(type).textBgColor;
    }
}
