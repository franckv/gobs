package com.gobs.ui.gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;
import com.gobs.ui.GUI;
import com.gobs.ui.GUIStyle;

public abstract class GdxGUI extends GUI<Color, BitmapFont> {
    private Batch batch;

    private ObjectMap<String, BitmapFont> fonts;
    public ObjectMap<String, GUIStyle<Color, BitmapFont>> styles;

    public GdxGUI(Batch batch) {
        super();

        this.batch = batch;

        style = new GUIStyle<Color, BitmapFont>();

        style.getLabelFormat().setTextColor(Color.WHITE);
        style.getHeaderFormat().setTextColor(Color.WHITE);
        style.getButtonFormat().setTextColor(Color.WHITE);
        style.getButtonSelectedFormat().setTextColor(Color.WHITE);
        style.getListItemFormat().setTextColor(Color.WHITE);
        style.getListItemSelectedFormat().setTextColor(Color.WHITE);

        style.getLabelFormat().setTextBgColor(Color.CLEAR);
        style.getHeaderFormat().setTextBgColor(Color.CLEAR);
        style.getButtonFormat().setTextBgColor(Color.DARK_GRAY);
        style.getButtonSelectedFormat().setTextBgColor(Color.LIGHT_GRAY);
        style.getListItemFormat().setTextBgColor(Color.CLEAR);
        style.getListItemSelectedFormat().setTextBgColor(Color.LIGHT_GRAY);

        styles = new ObjectMap<>();
        styles.put("default", style);

        fonts = new ObjectMap<>();
    }

    @Override
    public void begin() {
        super.begin();
    }

    public void addFont(String name, BitmapFont font) {
        if (fonts.size == 0) {
            style.setFont(font);
        }

        fonts.put(name, font);
    }

    public BitmapFont getFont(String name) {
        return fonts.get(name);
    }

    public void addStyle(String name, GUIStyle<Color, BitmapFont> style) {
        styles.put(name, style);
    }

    public void selectStyle(String name) {
        if (styles.containsKey(name)) {
            style = styles.get(name);
        }
    }

    @Override
    public float getLabelWidth(String text, BitmapFont font) {
        if (font == null) {
            throw new RuntimeException("Must supply font");
        }

        GlyphLayout glayout = new GlyphLayout(font, text);

        return glayout.width;
    }

    @Override
    public float getLabelHeight(String text, BitmapFont font) {
        if (font == null) {
            throw new RuntimeException("Must supply font");
        }

        GlyphLayout glayout = new GlyphLayout(font, text);

        return glayout.height;
    }

    @Override
    public void drawText(String text, float x, float y, BitmapFont font, Color color) {
        font.setColor(color);

        GlyphLayout glayout = new GlyphLayout(font, text);

        font.draw(batch, glayout, x, y + glayout.height);
    }

    @Override
    public void drawBox(float x, float y, float w, float h, Color color) {
        TextureRegion r = getSolidTexture(color);

        batch.draw(r, x, y, w, h);
    }

    @Override
    public void drawImage(String res, float x, float y, float w, float h) {
        TextureRegion img = getImage(res);

        batch.draw(img, x, y, w, h);
    }

    protected abstract TextureRegion getSolidTexture(Color color);

    protected abstract TextureRegion getFrame(boolean selected);

    protected abstract TextureRegion getImage(String resource);
}
