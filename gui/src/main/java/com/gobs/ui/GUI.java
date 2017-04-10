package com.gobs.ui;

import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Deque;

public abstract class GUI<Color, Font> {
    public enum FontSize {
        SMALL, MEDIUM, LARGE
    }

    private String hot, active;

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
        if (!isMouseDown()) {
            active = null;
        } else {
            if (active == null) {
                active = "Invalid";
            }
        }
    }

    public void load(Reader file) {
        getGUILoader().load(file);
    }

    private boolean isSelected(String id, float x, float y, float w, float h) {
        if ((getMouseX() >= x && getMouseX() <= x + w)
                && (getMouseY() >= y && getMouseY() <= y + h)) {
            hot = id;
            if (active == null && isMouseDown()) {
                active = id;
            }
            return true;
        }

        return false;
    }

    private boolean isClicked(String id) {
        if (!isMouseDown() && id.equals(hot) && id.equals(active)) {
            System.out.println(id + " is clicked");
            return true;
        }

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

    public void pushToEnd(String label) {
        switch (layout.getFlowDirection()) {
            case HORIZONTAL:
                pushToEnd(getLabelWidth(label));
                break;
            case VERTICAL:
                pushToEnd(getLabelHeight(label));
                break;
        }
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

    public abstract void addFont(String fontName, Font font);

    public abstract void setFont(String fontName);

    public abstract void setFontColor(Color color);

    public abstract GUILoader getGUILoader();

    public abstract void showFragment(String fragment);

    public abstract void enableFragment(String fragment, boolean enabled);

    public abstract void setStringValue(String id, String field, String value);

    public abstract void setIntValue(String id, String field, int value);

    public abstract Color getColorByName(String name);

    protected abstract boolean isMouseDown();

    protected abstract int getMouseX();

    protected abstract int getMouseY();
}
