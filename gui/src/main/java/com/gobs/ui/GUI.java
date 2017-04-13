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
            return true;
        }

        return false;
    }

    public void Label(String text) {
        Label(text, false);
    }

    private void Label(String text, boolean selected) {
        float h = getLabelHeight(text);
        float w = getLabelWidth(text);

        drawText(text, layout.getX(w), layout.getY(h), selected);

        layout.update(w, h);
    }

    public int Table(String id, String header, Iterable<String> values, int selected) {
        createSection(header, GUILayout.FlowDirection.VERTICAL);

        Label(header);

        int i = 0;
        for (String value : values) {
            float w = getLabelWidth(value);
            float h = getLabelHeight(value);

            float x = layout.getX(w);
            float y = layout.getY(h);
            float spacing = layout.getSpacing();

            boolean highlight = false;
            if (isSelected(id + "#" + i, x, y - spacing / 2, w, h + spacing) || i == selected) {
                highlight = true;
            }
            if (isClicked(id + "#" + i)) {
                selected = i;
            }
            Label(value, highlight);
            i++;
        }

        endSection();

        return selected;
    }

    public void Spacer(float width, float height) {
        layout.update(width, height);
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

    public boolean Button(String id, float w, float h, String label) {
        createSection(id, GUILayout.FlowDirection.NONE);

        boolean result = Box(id, w, h);

        float lw = getLabelWidth(label);
        float lh = getLabelHeight(label);

        layout.center(lw, lh);

        Label(label);

        endSection();

        return result;
    }

    public void createSection(String name, GUILayout.FlowDirection flow) {
        pushLayout(createLayout(name, layout, flow, getMaxWidth(), getMaxHeight()));
    }

    public void endSection() {
        layout.end();

        disposeLayout(layout);

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

    private void popLayout() {
        layouts.removeFirst();
        layout = layouts.getFirst();
    }

    protected GUILayout createLayout(String name, GUILayout parent, GUILayout.FlowDirection direction, float width, float height) {
        return new GUILayout(name, parent, direction, width, height);
    }

    protected void disposeLayout(GUILayout layout) {
    }

    protected GUILayout getLayout() {
        return layout;
    }

    public void setStringValue(String id, String field, String value) {
        getGUILoader().setStringValue(id, field, value);
    }

    public void setIntValue(String id, String field, int value) {
        getGUILoader().setIntValue(id, field, value);
    }

    public void setListValue(String id, String field, Iterable<String> values) {
        getGUILoader().setListValue(id, field, values);
    }

    public int getListSelection(String id) {
        return getGUILoader().getListSelection(id);
    }

    public abstract float getMaxWidth();

    public abstract float getMaxHeight();

    public abstract float getLabelWidth(String text);

    public abstract float getLabelHeight(String text);

    public abstract void drawText(String text, float x, float y, boolean selected);

    public abstract void drawBox(float x, float y, float w, float h, boolean selected);

    public abstract void addFont(String fontName, Font font);

    public abstract void setFont(String fontName);

    public abstract void setFontColor(Color color);

    public abstract void showFragment(String fragment);

    public abstract void enableFragment(String fragment, boolean enabled);

    public abstract void toggleFragment(String fragment);

    public abstract Color getColorByName(String name);

    protected abstract GUILoader getGUILoader();

    protected abstract boolean isMouseDown();

    protected abstract int getMouseX();

    protected abstract int getMouseY();
}
