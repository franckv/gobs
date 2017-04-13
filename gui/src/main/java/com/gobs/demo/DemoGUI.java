package com.gobs.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.gobs.ui.GUILayout;
import com.gobs.ui.gdx.GdxGUI;
import java.util.ArrayList;
import java.util.List;

public class DemoGUI extends GdxGUI {
    private OrthographicCamera camera;
    private ShapeRenderer renderer;

    private List<GUILayout> layouts;

    private boolean showLayouts = false;

    private int idx = 0;

    private Color[] palette;
    private Pixmap colorsMap;
    private Texture colorsTexture;

    private int mouseX, mouseY;
    private boolean mouseDown;

    private float maxWidth, maxHeight;

    public DemoGUI() {
        palette = new Color[]{
            Color.GREEN, Color.RED, Color.BLUE, Color.YELLOW,
            Color.PINK, Color.ORANGE, Color.MAGENTA, Color.PURPLE,
            Color.CYAN, Color.GRAY, Color.LIGHT_GRAY, Color.CLEAR
        };

        initTextures();
    }

    @Override
    public void begin() {
        super.begin();

        if (showLayouts) {
            layouts = new ArrayList<>();
            idx = 0;
        }
    }

    @Override
    public void end() {
        super.end();

        if (showLayouts) {
            drawLayouts();
        }
    }

    private void initTextures() {
        colorsMap = new Pixmap(16 * palette.length, 16, Pixmap.Format.RGBA8888);

        for (int i = 0; i < palette.length; i++) {
            colorsMap.setColor(palette[i]);
            colorsMap.fillRectangle(i * 16, 0, 16, 16);
        }

        colorsTexture = new Texture(colorsMap);
    }

    void toggleLayout() {
        showLayouts = !showLayouts;
    }

    void resize(float width, float height) {
        this.maxWidth = width;
        this.maxHeight = height;
    }

    void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }

    private void drawLayouts() {
        for (int i = layouts.size() - 1; i >= 0; i--) {
            GUILayout layout = layouts.get(i);

            idx = (idx + 1) % palette.length;

            drawSquare(layout.getLeft(), layout.getBottom(), layout.getRight(), layout.getTop(), palette[idx]);
        }

        layouts = null;
    }

    protected void setShowLayouts(boolean showLayouts) {
        this.showLayouts = showLayouts;
    }

    protected ShapeRenderer getRenderer() {
        if (renderer == null) {
            renderer = new ShapeRenderer();
        }
        return renderer;
    }

    public void drawSquare(float x1, float y1, float x2, float y2, Color color) {
        Gdx.gl20.glLineWidth(1);
        getRenderer().setProjectionMatrix(camera.combined);
        getRenderer().begin(ShapeRenderer.ShapeType.Line);
        getRenderer().setColor(color);

        getRenderer().line(x1, y1, x1, y2);
        getRenderer().line(x1, y2, x2, y2);
        getRenderer().line(x2, y2, x2, y1);
        getRenderer().line(x2, y1, x1, y1);

        getRenderer().end();
    }

    public void showCenters(int unit) {
        Gdx.gl20.glLineWidth(1);
        getRenderer().setProjectionMatrix(camera.combined);
        getRenderer().begin(ShapeRenderer.ShapeType.Line);
        getRenderer().setColor(Color.RED);

        int size = unit / 2;

        // vertical
        getRenderer().line(maxWidth / 2.0f, 0, maxWidth / 2.0f, maxHeight);

        for (int i = 0; i < maxHeight / unit; i++) {
            float y = i * unit;
            getRenderer().line(maxWidth / 2.0f - size, y, maxWidth / 2.0f + size, y);
        }

        // horizontal
        getRenderer().line(0, maxHeight / 2.0f, maxWidth, maxHeight / 2.0f);

        for (int i = 0; i < maxWidth / unit; i++) {
            float x = i * unit;
            getRenderer().line(x, maxHeight / 2.0f - size, x, maxHeight / 2.0f + size);
        }

        getRenderer().end();
    }

    public void showRuler(int unit) {
        Gdx.gl20.glLineWidth(1);
        getRenderer().setProjectionMatrix(camera.combined);
        getRenderer().begin(ShapeRenderer.ShapeType.Line);
        getRenderer().setColor(Color.RED);

        int size = unit;

        for (int i = 0; i < maxHeight / unit; i++) {
            float y = maxHeight - i * unit;
            getRenderer().line(0, y, size, y);
        }

        for (int i = 0; i < maxWidth / unit; i++) {
            float x = i * unit;
            getRenderer().line(x, maxHeight, x, maxHeight - size);
        }

        getRenderer().end();
    }

    @Override
    protected void disposeLayout(GUILayout layout) {
        if (showLayouts) {
            layouts.add(layout);
        }
    }

    @Override
    protected TextureRegion getSolidTexture(Color color) {
        for (int i = 0; i < palette.length; i++) {
            if (palette[i] == color) {
                return new TextureRegion(colorsTexture, i * 16, 0, 16, 16);
            }
        }

        return null;
    }

    @Override
    protected TextureRegion getFrame(boolean selected) {
        if (selected) {
            return getSolidTexture(Color.GRAY);
        } else {
            return getSolidTexture(Color.LIGHT_GRAY);
        }
    }

    @Override
    protected TextureRegion getLabelBg(boolean selected) {
        if (selected) {
            return getSolidTexture(Color.LIGHT_GRAY);
        } else {
            return getSolidTexture(Color.CLEAR);
        }
    }

    @Override
    public float getMaxWidth() {
        return maxWidth;
    }

    @Override
    public float getMaxHeight() {
        return maxHeight;
    }

    @Override
    protected boolean isMouseDown() {
        return mouseDown;
    }

    @Override
    protected int getMouseX() {
        return mouseX;
    }

    @Override
    protected int getMouseY() {
        return mouseY;
    }

    void setMouseDown(boolean mouseDown) {
        this.mouseDown = mouseDown;
    }

    void setMousePosition(int x, int y) {
        this.mouseX = x;
        this.mouseY = y;
    }

    @Override
    public void dispose() {
        super.dispose();

        colorsMap.dispose();
        colorsTexture.dispose();

        if (renderer != null) {
            renderer.dispose();
        }
    }
}
