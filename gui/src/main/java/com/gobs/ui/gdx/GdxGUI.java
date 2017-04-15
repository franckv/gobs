package com.gobs.ui.gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.gobs.ui.GUI;
import com.gobs.ui.GUIStyle;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class GdxGUI extends GUI<Color, BitmapFont> implements Disposable {
    private Batch batch;
    private GdxGUILoader JsonLoader;
    private GdxGUIStylist stylist;

    public GdxGUI(Batch batch) {
        this.batch = batch;
        JsonLoader = new GdxGUILoader(this);

        stylist = new GdxGUIStylist();
    }

    @Override
    public void begin() {
        super.begin();
    }

    public void addFont(String name, BitmapFont font) {
        stylist.addFont(name, font);
    }

    public BitmapFont getFont(GUI.GUIElement type) {
        return stylist.getFont(type);
    }

    public TextureRegion getBackground(GUI.GUIElement type) {
        Color color = stylist.getBackgroundColor(type);

        return getSolidTexture(color);
    }

    public BitmapFont getFont(String name) {
        return stylist.getFont(name);
    }

    public GUIStyle<Color, BitmapFont> createStyle(String name) {
        return stylist.createStyle(name);
    }

    public GUIStyle<Color, BitmapFont> createStyle(String name, GUIStyle<Color, BitmapFont> parent) {
        return stylist.createStyle(name, parent);
    }

    public void resetStyle() {
        stylist.resetStyle();
    }

    @Override
    public void selectStyle(String name) {
        stylist.selectStyle(name);
    }

    public GUIStyle<Color, BitmapFont> getStyle() {
        return stylist.getCurrentStyle();
    }

    @Override
    public float getLabelWidth(String text, GUIElement type) {
        BitmapFont font = getFont(type);

        if (font == null) {
            throw new RuntimeException("Must supply font");
        }

        GlyphLayout glayout = new GlyphLayout(font, text);

        return glayout.width;
    }

    @Override
    public float getLabelHeight(String text, GUIElement type) {
        BitmapFont font = getFont(type);

        if (font == null) {
            throw new RuntimeException("Must supply font");
        }

        GlyphLayout glayout = new GlyphLayout(font, text);

        return glayout.height;
    }

    @Override
    public void drawText(String text, float x, float y, GUIElement type) {
        BitmapFont font = getFont(type);

        GlyphLayout glayout = new GlyphLayout(font, text);

        float spacing = getLayout().getSpacing();

        batch.draw(getBackground(type), x, y - spacing * 0.4f, glayout.width, glayout.height + spacing * 0.8f);

        font.draw(batch, glayout, x, y + glayout.height);
    }

    @Override
    public void drawBox(float x, float y, float w, float h, GUIElement type) {
        TextureRegion r = getBackground(type);

        batch.draw(r, x, y, w, h);
    }

    @Override
    public void drawImage(String res, float x, float y, float w, float h) {
        TextureRegion img = getImage(res);

        batch.draw(img, x, y, w, h);
    }

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
        stylist.dispose();
    }

    protected abstract TextureRegion getSolidTexture(Color color);

    protected abstract TextureRegion getFrame(boolean selected);

    protected abstract TextureRegion getImage(String resource);
}
