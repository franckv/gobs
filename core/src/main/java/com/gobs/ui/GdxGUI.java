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
import com.gobs.display.OrthographicDisplay;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class GdxGUI extends GUI<Color, BitmapFont> implements Disposable {
    private Batch batch;
    private Map<String, BitmapFont> fonts;
    private BitmapFont font;
    private Color color;
    private ShapeRenderer renderer;
    private TileFactory tileManager;
    private OrthographicDisplay display;
    private JsonGUILoader JsonLoader;
    
    public GdxGUI(OrthographicDisplay display, TileFactory tileManager, Batch batch) {
        this.display = display;
        this.tileManager = tileManager;
        this.batch = batch;

        fonts = new HashMap<>();

        color = Color.GREEN;
        
        JsonLoader = new JsonGUILoader(this);
    }

    @Override
    public void addFont(String name, BitmapFont font) {
        fonts.put(name, font);
        this.font = font;
    }

    @Override
    public void setFont(String name) {
        if (fonts.containsKey(name)) {
            font = fonts.get(name);
        }
    }

    @Override
    public void setFontColor(Color color) {
        this.color = color;
    }

    @Override
    public float getMaxWidth() {
        return display.getViewPort().getWorldWidth();
    }

    @Override
    public float getMaxHeight() {
        return display.getViewPort().getWorldHeight();
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
        TextureRegion r = tileManager.getFrame();

        if (selected) {
            r = tileManager.getFrameSelected();
        }

        batch.draw(r, x, y, w, h);
    }

    @Override
    public void load(String resource) {
        JsonLoader.load(Gdx.files.internal(resource).reader());
    }
    
    @Override
    public void showFragment(String fragment, Map<String, String> resolver) {
        JsonLoader.showFragment(fragment, resolver);
    }

    private ShapeRenderer getRenderer() {
        if (renderer == null) {
            renderer = new ShapeRenderer();
        }
        return renderer;
    }

    public void drawSquare(float x1, float y1, float x2, float y2) {
        Gdx.gl20.glLineWidth(5);
        getRenderer().setProjectionMatrix(display.getCamera().combined);
        getRenderer().begin(ShapeRenderer.ShapeType.Line);
        getRenderer().setColor(Color.GREEN);

        getRenderer().line(x1, y1, x1, y2);
        getRenderer().line(x1, y2, x2, y2);
        getRenderer().line(x2, y2, x2, y1);
        getRenderer().line(x2, y1, x1, y1);

        getRenderer().end();
    }

    public void showCenters() {
        Gdx.gl20.glLineWidth(1);
        getRenderer().setProjectionMatrix(display.getCamera().combined);
        getRenderer().begin(ShapeRenderer.ShapeType.Line);
        getRenderer().setColor(Color.RED);
        getRenderer().line(Gdx.graphics.getWidth() / 2.0f, 0, Gdx.graphics.getWidth() / 2.0f, Gdx.graphics.getHeight());
        getRenderer().line(0, Gdx.graphics.getHeight() / 2.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2.0f);

        getRenderer().end();
    }

    @Override
    public void dispose() {
        for (BitmapFont font : fonts.values()) {
            font.dispose();
        }
        if (renderer != null) {
            renderer.dispose();
        }
    }
}
