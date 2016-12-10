package com.gobs.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
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

    private static InputMap inputs;

    String hot, active;

    private GUILayout layout;
    private Deque<GUILayout> layouts;

    private Batch batch;
    private Map<String, BitmapFont> fonts;
    private BitmapFont font;
    private Color color;
    private TextureRegion frame;
    private TextureRegion frameSelected;
    private ShapeRenderer render;

    public GUI(Batch batch) {
        this.batch = batch;

        hot = null;
        active = null;

        fonts = new HashMap<>();

        color = Color.GREEN;

        frame = GameState.getTileManager().getFrame();
        frameSelected = GameState.getTileManager().getFrameSelected();

        render = new ShapeRenderer();

        begin();
    }

    public void begin() {
        layout = new GUILayout("root", null, GUILayout.FlowDirection.VERTICAL);
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
    
    private boolean isSelected(String id, Rectangle rec) {
        if (inputs != null && rec.contains(inputs.getMouseX(), inputs.getMouseY())) {
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

    public void addFont(String name, BitmapFont font) {
        fonts.put(name, font);
        this.font = font;
    }

    public void setFont(String name) {
        if (fonts.containsKey(name)) {
            font = fonts.get(name);
        }
    }

    public static boolean AcceptInput(InputMap inputMap) {
        GUI.inputs = inputMap;

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

        layout.update(w, h);
    }

    public void Spacer(float size) {
        layout.space(size);
    }

    public void Frame(float w, float h) {
        batch.draw(frame, layout.getX(w), layout.getY(h), w, h);

        layout.update(w, h);
    }

    public boolean Box(String id, float w, float h) {
        Rectangle rec = new Rectangle(layout.getX(w), layout.getY(h), w, h);

        TextureRegion r = frame;

        if (isSelected(id, rec)) {
            r = frameSelected;
        }

        batch.draw(r, rec.x, rec.y, w, h);

        layout.update(w, h);

        return isClicked(id);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void createSection(String name, GUILayout.FlowDirection flow) {
        pushLayout(new GUILayout(name, layout, flow));
    }

    public void endSection() {
        layout.end();
        popLayout();
    }

    public void SetPosition(float x, float y) {
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
