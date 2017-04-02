package com.gobs.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.gobs.assets.TileFactory;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class GdxGUI extends GUI implements Disposable {
    private Batch batch;
    private Map<String, BitmapFont> fonts;
    private BitmapFont font;
    private Color color;
    private TextureRegion frame;
    private TextureRegion frameSelected;
    private ShapeRenderer render;
    private TileFactory tileManager;
    private DisplayManager displayManager;

    public GdxGUI(DisplayManager displayManager, TileFactory tileManager, Batch batch) {
        this.displayManager = displayManager;
        this.tileManager = tileManager;
        this.batch = batch;

        fonts = new HashMap<>();

        color = Color.GREEN;

        frame = tileManager.getFrame();
        frameSelected = tileManager.getFrameSelected();

        render = new ShapeRenderer();
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

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public float getMaxWidth() {
        return displayManager.getOverlayViewport().getWorldWidth();
    }

    @Override
    public float getMaxHeight() {
        return displayManager.getOverlayViewport().getWorldHeight();
    }

    @Override
    public float getLabelWidth(String text) {
        GlyphLayout glayout = new GlyphLayout(font, text);

        return glayout.width;
    }

    @Override
    public float getLabelHeight(String text) {
        GlyphLayout glayout = new GlyphLayout(font, text);

        return glayout.height;
    }

    @Override
    public void drawText(String text, float x, float y) {
        font.setColor(color);

        GlyphLayout glayout = new GlyphLayout(font, text);

        font.draw(batch, glayout, x, y + glayout.height);
    }

    @Override
    public void drawBox(float x, float y, float w, float h, boolean selected) {
        TextureRegion r = frame;

        if (selected) {
            r = frameSelected;
        }

        batch.draw(r, x, y, w, h);
    }

    public void drawSquare(float x1, float y1, float x2, float y2) {
        Gdx.gl20.glLineWidth(5);
        render.setProjectionMatrix(displayManager.getOverlayCamera().combined);
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
        render.setProjectionMatrix(displayManager.getOverlayCamera().combined);
        render.begin(ShapeRenderer.ShapeType.Line);
        render.setColor(Color.RED);
        render.line(Gdx.graphics.getWidth() / 2.0f, 0, Gdx.graphics.getWidth() / 2.0f, Gdx.graphics.getHeight());
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
