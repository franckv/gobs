package com.gobs.ui;

public abstract class GUI<Color, Font> {
    private String hot, active;

    private GUILayout layout;
    private GUILayout[] layouts;
    private int layoutIdx = 0;
    private static final int MAX_LAYOUTS = 100;

    protected GUIStyle<Color, Font> style;

    public GUI() {
        hot = null;
        active = null;
        style = null;

        layouts = new GUILayout[MAX_LAYOUTS];
        layout = null;
        layouts[0] = null;
        layoutIdx = 0;
    }

    public void setStyle(GUIStyle<Color, Font> style) {
        this.style = style;
    }

    public GUIStyle<Color, Font> getStyle() {
        return style;
    }

    public void begin() {
        layoutIdx = 0;

        if (layouts[0] == null) {
            layouts[0] = new GUILayout("root", null, GUILayout.FlowDirection.VERTICAL, getMaxWidth(), getMaxHeight());
        } else {
            layouts[0].init();
        }
        layout = layouts[0];

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
        Label(text, style.getLabelFormat());
    }

    private void Label(String text, GUIFormat<Color, Font> format) {
        float h = getLabelHeight(text, format.getTextFont());
        float w = getLabelWidth(text, format.getTextFont());

        float spacing = layout.getSpacing();

        drawBox(layout.getX(w), layout.getY(h) - spacing * 0.4f, w, h + spacing * 0.8f, format.getTextBgColor());

        drawText(text, layout.getX(w), layout.getY(h), format.getTextFont(), format.getTextColor());

        layout.update(w, h);
    }

    public int Table(String id, String header, Iterable<String> values, int selected) {
        createSection(header, GUILayout.FlowDirection.VERTICAL);

        Label(header, style.getHeaderFormat());

        int i = 0;
        for (String value : values) {
            // assumes selected items have the same size as unselected
            float w = getLabelWidth(value, style.getListItemFormat().getTextFont());
            float h = getLabelHeight(value, style.getListItemFormat().getTextFont());

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
            Label(value, highlight ? style.getListItemSelectedFormat() : style.getListItemFormat());
            i++;
        }

        endSection();

        return selected;
    }

    public void Spacer(float width, float height) {
        layout.update(width, height);
    }

    public void Frame(float w, float h, Color color) {
        drawBox(layout.getX(w), layout.getY(h), w, h, color);
        layout.update(w, h);
    }

    public boolean Box(String id, float w, float h) {
        float x = layout.getX(w);
        float y = layout.getY(h);

        boolean selected = isSelected(id, x, y, w, h);

        drawBox(x, y, w, h, selected ? style.getButtonSelectedFormat().getTextBgColor() : style.getButtonFormat().getTextBgColor());

        layout.update(w, h);

        return isClicked(id);
    }

    public boolean Button(String id, float w, float h, String label) {
        createSection(id, GUILayout.FlowDirection.NONE);

        boolean result = Box(id, w, h);

        float lw = getLabelWidth(label, style.getButtonFormat().getTextFont());
        float lh = getLabelHeight(label, style.getButtonFormat().getTextFont());

        layout.center(lw, lh);

        Label(label, hot == id ? style.getButtonSelectedFormat() : style.getButtonFormat());

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
                pushToEnd(getLabelWidth(label, style.getLabelFormat().getTextFont()));
                break;
            case VERTICAL:
                pushToEnd(getLabelHeight(label, style.getLabelFormat().getTextFont()));
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
        layoutIdx++;
        assert layoutIdx < MAX_LAYOUTS;

        this.layout = layout;
        layouts[layoutIdx] = layout;
        //layouts.addFirst(layout);
    }

    private void popLayout() {
        layoutIdx--;
        assert layoutIdx >= 0;

        layout = layouts[layoutIdx];
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

    public abstract float getLabelWidth(String text, Font font);

    public abstract float getLabelHeight(String text, Font font);

    public abstract void drawText(String text, float x, float y, Font font, Color color);

    public abstract void drawBox(float x, float y, float w, float h, Color color);

    public abstract void drawImage(String res, float x, float y, float w, float h);

    protected abstract boolean isMouseDown();

    protected abstract int getMouseX();

    protected abstract int getMouseY();
}
