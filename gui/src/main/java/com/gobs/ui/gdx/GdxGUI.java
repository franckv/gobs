package com.gobs.ui.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.gobs.ui.GUI;
import com.gobs.ui.GUILoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class GdxGUI extends GUI<Color, BitmapFont> implements Disposable {
    private Batch batch;
    private ObjectMap<String, BitmapFont> fonts;
    private BitmapFont font;
    private Color color;
    private ShapeRenderer renderer;
    private GdxGUILoader JsonLoader;

    public GdxGUI() {
        fonts = new ObjectMap<>();

        color = Color.GREEN;

        JsonLoader = new GdxGUILoader(this);
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
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
    public float getLabelWidth(String text) {
        if (font == null) {
            throw new RuntimeException("Must supply font");
        }

        GlyphLayout glayout = new GlyphLayout(font, text);

        return glayout.width;
    }

    @Override
    public float getLabelHeight(String text) {
        if (font == null) {
            throw new RuntimeException("Must supply font");
        }

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
        TextureRegion r = getFrame(selected);

        batch.draw(r, x, y, w, h);
    }

    @Override
    public GUILoader getGUILoader() {
        return JsonLoader;
    }

    @Override
    public void showFragment(String fragment) {
        JsonLoader.showFragment(fragment);
    }

    @Override
    public void enableFragment(String fragment, boolean enabled) {
        JsonLoader.enableFragment(fragment, enabled);
    }

    @Override
    public void toggleFragment(String fragment) {
        JsonLoader.toggleFragment(fragment);
    }

    @Override
    public void setStringValue(String id, String field, String value) {
        JsonLoader.setStringValue(id, field, value);
    }

    @Override
    public void setIntValue(String id, String field, int value) {
        JsonLoader.setIntValue(id, field, value);
    }

    @Override
    public Color getColorByName(String name) {
        Color color = Color.GREEN;

        try {
            color = (Color) Color.class
                    .getDeclaredField(name.toUpperCase()).get(null);
        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(GdxGUI.class
                    .getName()).log(Level.SEVERE, "Invalid color " + name, ex);
        }

        return color;
    }

    protected ShapeRenderer getRenderer() {
        if (renderer == null) {
            renderer = new ShapeRenderer();
        }
        return renderer;
    }

    public void drawSquare(float x1, float y1, float x2, float y2, Color color) {
        Gdx.gl20.glLineWidth(1);
        getRenderer().setProjectionMatrix(getCamera().combined);
        getRenderer().begin(ShapeRenderer.ShapeType.Line);
        getRenderer().setColor(color);

        getRenderer().line(x1, y1, x1, y2);
        getRenderer().line(x1, y2, x2, y2);
        getRenderer().line(x2, y2, x2, y1);
        getRenderer().line(x2, y1, x1, y1);

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

    protected abstract TextureRegion getFrame(boolean selected);

    protected abstract OrthographicCamera getCamera();
}
