package com.gobs.ui.gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    private Color fontColor;
    private GdxGUILoader JsonLoader;

    public GdxGUI() {
        fonts = new ObjectMap<>();

        fontColor = Color.GREEN;

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
        this.fontColor = color;
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
    public void drawText(String text, float x, float y, boolean selected) {
        font.setColor(fontColor);

        GlyphLayout glayout = new GlyphLayout(font, text);

        float spacing = getLayout().getSpacing();

        batch.draw(getLabelBg(selected), x, y - spacing / 2, glayout.width, glayout.height + spacing);

        font.draw(batch, glayout, x, y + glayout.height);
    }

    @Override
    public void drawBox(float x, float y, float w, float h, boolean selected) {
        TextureRegion r = getFrame(selected);

        batch.draw(r, x, y, w, h);
    }

    @Override
    protected GUILoader getGUILoader() {
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

    @Override
    public void dispose() {
        for (BitmapFont font : fonts.values()) {
            font.dispose();
        }
    }

    protected abstract TextureRegion getSolidTexture(Color color);

    protected abstract TextureRegion getLabelBg(boolean selected);

    protected abstract TextureRegion getFrame(boolean selected);
}
