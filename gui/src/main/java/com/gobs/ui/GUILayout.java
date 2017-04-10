package com.gobs.ui;

/**
 *
 */
public class GUILayout {

    public enum FlowDirection {
        NONE, HORIZONTAL, VERTICAL
    }

    private String id;

    private int nChildren;

    private GUILayout parent;

    private FlowDirection flow;

    private float top, bottom, left, right;

    private float posX, posY;

    private float maxWidth, maxHeight;

    private int marginX, marginY;
    private int spacing;

    GUILayout(String name, GUILayout parent, GUILayout.FlowDirection flow, float maxWidth, float maxHeight) {
        this.parent = parent;
        this.nChildren = 0;

        this.flow = flow;

        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;

        if (parent != null) {
            parent.nChildren++;
            this.id = parent.id + ":" + name;

            this.marginX = parent.marginX;
            this.marginY = parent.marginY;
            this.spacing = parent.spacing;

            switch (parent.flow) {
                case HORIZONTAL:
                    this.left = parent.right;
                    this.top = parent.top;
                    break;
                case VERTICAL:
                    this.left = parent.left;
                    this.top = parent.bottom;
                    break;
                case NONE:
                    this.left = parent.left;
                    this.top = parent.top;
                    break;
            }
        } else {
            this.id = name;

            this.marginX = 0;
            this.marginY = 0;
            this.spacing = 0;

            this.top = maxHeight;
            this.left = 0;
        }

        this.bottom = top;
        this.right = left;

        this.posX = left;
        this.posY = top;
    }

    String getId() {
        return id;
    }

    float getX(float width) {
        return posX;
    }

    float getY(float height) {
        return posY - height;
    }

    FlowDirection getFlowDirection() {
        return flow;
    }

    void setPosition(float x, float y) {
        if (flow != FlowDirection.NONE) {
            return;
        }

        if (x < 0 || x > maxWidth || y < 0 || y > maxHeight) {
            return;
        }

        posX = x;
        posY = y;
        top = y;
        bottom = y;
        left = x;
        right = x;
    }

    void update(float width, float height) {
        switch (flow) {
            case VERTICAL:
                if (width > right - left) {
                    right = Math.min(left + width, maxWidth);
                }
                bottom = Math.max(0, posY - height);
                posY = Math.max(0, bottom - spacing);

                break;
            case HORIZONTAL:
                if (height > top - bottom) {
                    bottom = Math.max(0, top - height);
                }
                right = Math.min(posX + width, maxWidth);
                posX = Math.min(right + spacing, maxWidth);
                break;
            case NONE:
                if (height > top - bottom) {
                    bottom = Math.max(0, top - height);
                }
                if (width > right - left) {
                    right = Math.min(left + width, maxWidth);
                }
                break;
        }
    }

    void end() {
        if (parent != null) {
            if (parent.right < right) {
                parent.right = right;
            }
            if (parent.bottom > bottom) {
                parent.bottom = bottom;
            }

            switch (parent.flow) {
                case HORIZONTAL:
                    parent.right = Math.min(parent.right + spacing, maxWidth);
                    parent.posX = parent.right;
                    parent.posY = parent.top;
                    break;
                case VERTICAL:
                    parent.bottom = Math.max(0, parent.bottom - spacing);
                    parent.posX = parent.left;
                    parent.posY = parent.bottom;
                    break;
            }
        }
    }

    void space(float size) {

        switch (flow) {
            case VERTICAL:
                update(0, size);
                break;
            case HORIZONTAL:
                update(size, 0);
                break;
        }
    }

    void pushToEnd(float size) {
        switch (flow) {
            case HORIZONTAL:
                posX = maxWidth - size - marginX;
                right = posX;
                break;
            case VERTICAL:
                posY = size + marginY;
                bottom = posY;
                break;
        }
    }

    void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    void setMargin(int x, int y) {
        this.marginX = x;
        this.marginY = y;

        this.left += x;
        this.right += x;
        this.posX = left;

        this.top -= y;
        this.bottom -= y;
        this.posY = top;
    }

}
