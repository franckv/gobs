package com.gobs.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.gobs.GameState;
import com.gobs.input.InputMap;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class GUI implements Disposable {
    public enum FontSize {
        SMALL, MEDIUM, LARGE
    }

    private GUILayout layout;
    private Deque<GUILayout> layouts;

    private Batch batch;
    private Map<String, BitmapFont> fonts;
    private BitmapFont font;
    private Color color;
    private TextureRegion frame;
    private ShapeRenderer render;

    public GUI(Batch batch) {
        this.batch = batch;

        fonts = new HashMap<>();

        color = Color.GREEN;

        frame = GameState.getTileManager().getFrame();

        render = new ShapeRenderer();

        init();
    }

    public void init() {
        layout = new GUILayout("root", null, GUILayout.FlowDirection.VERTICAL);
        layouts = new ArrayDeque<>();
        layouts.addFirst(layout);
    }

    public void addFont(String name, BitmapFont font) {
        fonts.put(name, font);
        this.font = font;
    }

    public void setFont(String name) {
        if (fonts.containsKey(name)) {
            font = fonts.get(name);
        }
    }

    public boolean AcceptInput(InputMap inputMap) {
        return false;
    }

    public boolean Button() {
        return false;
    }

    public float getLabelWidth(String text) {
        GlyphLayout glayout = new GlyphLayout(font, text);

        return glayout.width;
    }

    public float getLabelHeight(String text) {
        GlyphLayout glayout = new GlyphLayout(font, text);

        return glayout.height;
    }

    public void Label(String text) {
        font.setColor(color);

        GlyphLayout glayout = new GlyphLayout(font, text);

        float h = glayout.height;
        float w = glayout.width;

        font.draw(batch, glayout, layout.getX(w), h + layout.getY(h));

        layout.updateLayout(w, h);
    }

    public void Spacer(float size) {
        layout.space(size);
    }

    public void Box(float w, float h) {
        batch.draw(frame, layout.getX(w), layout.getY(h), w, h);

        layout.updateLayout(w, h);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public GUILayout getLayout() {
        return layout;
    }

    public void createSection(String name, GUILayout.FlowDirection flow) {
        pushLayout(new GUILayout(name, layout, flow));
    }

    public void endSection() {
        layout.end();
        popLayout();
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

    public void drawSquare(float x1, float y1, float x2, float y2) {
        Gdx.gl20.glLineWidth(5);
        render.setProjectionMatrix(GameState.getOverlayCamera().combined);
        render.begin(ShapeRenderer.ShapeType.Line);
        render.setColor(Color.GREEN);

        render.line(x1, y1, x1, y2);
        render.line(x1, y2, x2, y2);
        render.line(x2, y2, x2, y1);
        render.line(x2, y1, x1, y1);

        render.end();
    }

    public void showCenters() {
        Gdx.gl20.glLineWidth(1);
        render.setProjectionMatrix(GameState.getOverlayCamera().combined);
        render.begin(ShapeRenderer.ShapeType.Line);
        render.setColor(Color.RED);
        render.line(Gdx.graphics.getWidth() / 2, 0, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight());
        render.line(0, Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2);

        render.end();
    }

    @Override
    public void dispose() {
        for (BitmapFont font : fonts.values()) {
            font.dispose();
        }
        render.dispose();
    }
}
