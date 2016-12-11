package com.gobs.ui;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 *
 */
public abstract class GUI {
    public enum FontSize {
        SMALL, MEDIUM, LARGE
    }

    private static InputMap inputs;

    String hot, active;

    private GUILayout layout;
    private Deque<GUILayout> layouts;

    public GUI() {
        hot = null;
        active = null;
    }

    public void begin() {
        layout = new GUILayout("root", null, GUILayout.FlowDirection.VERTICAL, getMaxWidth(), getMaxHeight());
        layouts = new ArrayDeque<>();
        layouts.addFirst(layout);

        hot = null;
    }

    public void end() {
        if (!isActivated()) {
            active = null;
        } else {
            if (active == null) {
                active = "Invalid";
            }
        }
    }

    
    
    private boolean isActivated() {
        return (inputs != null && inputs.isMouseDown());
    }

    private boolean isSelected(String id, float x, float y, float w, float h) {
        if (inputs != null
                && (inputs.getMouseX() >= x && inputs.getMouseX() <= x + w)
                && (inputs.getMouseY() >= y && inputs.getMouseY() <= y + h)) {
            hot = id;
            if (active == null && isActivated()) {
                active = id;
            }
            return true;
        }

        return false;
    }

    private boolean isClicked(String id) {
        if (inputs != null && !isActivated() && id.equals(hot) && id.equals(active)) {
            System.out.println(id + " is clicked");
            return true;
        }

        return false;
    }

    public static boolean acceptInput(InputMap inputMap) {
        GUI.inputs = inputMap;

        return false;
    }

    public boolean Button() {
        return false;
    }

    public void Label(String text) {
        float h = getLabelHeight(text);
        float w = getLabelWidth(text);

        drawText(text, layout.getX(w), layout.getY(h));

        layout.update(w, h);
    }

    public void Spacer(float size) {
        layout.space(size);
    }

    public void Frame(float w, float h) {
        drawBox(layout.getX(w), layout.getY(h), w, h, false);
        layout.update(w, h);
    }

    public boolean Box(String id, float w, float h) {
        float x = layout.getX(w);
        float y = layout.getY(h);

        boolean selected = isSelected(id, x, y, w, h);

        drawBox(x, y, w, h, selected);

        layout.update(w, h);

        return isClicked(id);
    }

    public void createSection(String name, GUILayout.FlowDirection flow) {
        pushLayout(new GUILayout(name, layout, flow, getMaxWidth(), getMaxHeight()));
    }

    public void endSection() {
        layout.end();
        popLayout();
    }

    public void setPosition(float x, float y) {
        layout.setPosition(x, y);
    }

    public void pushToEnd(float size) {
        layout.pushToEnd(size);
    }

    public void setMargin(int margin) {
        setMargin(margin, margin);
    }

    public void setMargin(int marginX, int marginY) {
        layout.setMargin(marginX, marginY);
    }

    public void setSpacing(int spacing) {
        layout.setSpacing(spacing);
    }

    private void pushLayout(GUILayout layout) {
        this.layout = layout;
        layouts.addFirst(layout);
    }

    public void popLayout() {
        layouts.removeFirst();
        layout = layouts.getFirst();
    }

    public abstract float getMaxWidth();
    
    public abstract float getMaxHeight();
    
    public abstract float getLabelWidth(String text);

    public abstract float getLabelHeight(String text);

    public abstract void drawText(String text, float x, float y);

    public abstract void drawBox(float x, float y, float w, float h, boolean selected);
}
