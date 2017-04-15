package com.gobs.ui;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class GUI<Color, Font> {
    public enum GUIElement {
        LABEL, HEADER, LIST_ITEM, LIST_ITEM_SELECTED, BUTTON, BUTTON_SELECTED, FRAME
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

    public void Image(String res, float w, float h) {
        drawImage(res, layout.getX(w), layout.getY(h), w, h);
        layout.update(w, h);
    }

    public boolean ImageBox(String id, String res, String res_selected, float w, float h) {
        float x = layout.getX(w);
        float y = layout.getY(h);

        boolean selected = isSelected(id, x, y, w, h);

        if (selected) {
            Image(res_selected, w, h);
        } else {
            Image(res, w, h);
        }

        return isClicked(id);
    }

    public void Label(String text) {
        Label(text, GUIElement.LABEL);
    }

    private void Label(String text, GUIElement type) {
        float h = getLabelHeight(text, type);
        float w = getLabelWidth(text, type);

        drawText(text, layout.getX(w), layout.getY(h), type);

        layout.update(w, h);
    }

    public int Table(String id, String header, Iterable<String> values, int selected) {
        createSection(header, GUILayout.FlowDirection.VERTICAL);

        Label(header, GUIElement.HEADER);

        int i = 0;
        for (String value : values) {
            // assumes selected items have the same size as unselected
            float w = getLabelWidth(value, GUIElement.LIST_ITEM);
            float h = getLabelHeight(value, GUIElement.LIST_ITEM);

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
            Label(value, highlight ? GUIElement.LIST_ITEM_SELECTED : GUIElement.LIST_ITEM);
            i++;
        }

        endSection();

        return selected;
    }

    public void Spacer(float width, float height) {
        layout.update(width, height);
    }

    public void Frame(float w, float h) {
        drawBox(layout.getX(w), layout.getY(h), w, h, GUIElement.FRAME);
        layout.update(w, h);
    }

    public boolean Box(String id, float w, float h) {
        float x = layout.getX(w);
        float y = layout.getY(h);

        boolean selected = isSelected(id, x, y, w, h);

        drawBox(x, y, w, h, selected ? GUIElement.BUTTON_SELECTED : GUIElement.BUTTON);

        layout.update(w, h);

        return isClicked(id);
    }

    public boolean Button(String id, float w, float h, String label) {
        createSection(id, GUILayout.FlowDirection.NONE);

        boolean result = Box(id, w, h);

        float lw = getLabelWidth(label, GUIElement.BUTTON);
        float lh = getLabelHeight(label, GUIElement.BUTTON);

        layout.center(lw, lh);

        Label(label, hot == id ? GUIElement.BUTTON_SELECTED : GUIElement.BUTTON);

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

    public void pushToEnd(String label, GUIElement type) {
        switch (layout.getFlowDirection()) {
            case HORIZONTAL:
                pushToEnd(getLabelWidth(label, type));
                break;
            case VERTICAL:
                pushToEnd(getLabelHeight(label, type));
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

    public abstract float getMaxWidth();

    public abstract float getMaxHeight();

    public abstract float getLabelWidth(String text, GUIElement type);

    public abstract float getLabelHeight(String text, GUIElement type);

    public abstract void drawText(String text, float x, float y, GUIElement type);

    public abstract void drawBox(float x, float y, float w, float h, GUIElement type);

    public abstract void drawImage(String res, float x, float y, float w, float h);

    public abstract void selectStyle(String name);

    protected abstract boolean isMouseDown();

    protected abstract int getMouseX();

    protected abstract int getMouseY();
}
