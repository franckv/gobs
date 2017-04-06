package com.gobs.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.gobs.Config;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory for color tiles
 */
public class TileFactory implements Disposable {
    private AssetManager assetManager;
    private int tileSize; // size in pixels
    private Pixmap pixmap;
    private Texture texture;
    private String frameSprite;
    private String frameSelectedSprite;

    private Map<Color, TextureRegion> fullTiles;
    private Map<Color, TextureRegion> rectTiles;

    public TileFactory(Config config) {
        this.assetManager = new AssetManager();
        this.tileSize = config.getTileSize();

        fullTiles = new HashMap<>();
        rectTiles = new HashMap<>();

        List<Color> colors = new ArrayList<>();

        colors.add(Color.DARK_GRAY);
        colors.add(Color.LIGHT_GRAY);
        colors.add(Color.RED);
        colors.add(Color.PINK);
        colors.add(Color.PURPLE);
        colors.add(Color.GREEN);
        colors.add(Color.WHITE);
        colors.add(Color.BLUE);
        colors.add(Color.YELLOW);
        colors.add(Color.GOLD);
        colors.add(Color.CLEAR);

        initTiles(colors);

        frameSprite = config.getFrameSprite();
        frameSelectedSprite = config.getFrameSelectedSprite();
    }

    private void initTiles(List<Color> colors) {
        pixmap = new Pixmap(colors.size() * tileSize, 2 * tileSize, Pixmap.Format.RGBA8888);

        for (int i = 0; i < colors.size(); i++) {
            Color color = colors.get(i);

            pixmap.setColor(color);
            pixmap.fillRectangle(i * tileSize, 0, tileSize, tileSize);
            pixmap.drawRectangle(i * tileSize, tileSize, tileSize, tileSize);
        }

        texture = new Texture(pixmap);

        for (int i = 0; i < colors.size(); i++) {
            Color color = colors.get(i);
            fullTiles.put(color, new TextureRegion(texture, i * tileSize, 0, tileSize, tileSize));
            rectTiles.put(color, new TextureRegion(texture, i * tileSize, tileSize, tileSize, tileSize));
        }
    }

    public TextureRegion getFullTile(Color color) {
        if (fullTiles.containsKey(color)) {
            return fullTiles.get(color);
        } else {
            return fullTiles.get(Color.DARK_GRAY);
        }
    }

    public TextureRegion getRectTile(Color color) {
        if (rectTiles.containsKey(color)) {
            return rectTiles.get(color);
        } else {
            return rectTiles.get(Color.DARK_GRAY);
        }
    }

    public TextureRegion getFrame() {
        return getTile(frameSprite);
    }

    public TextureRegion getFrameSelected() {
        return getTile(frameSelectedSprite);
    }

    public TextureRegion getTile(String res, int x, int y, int w, int h) {
        Texture tex = getTexture(res);

        return new TextureRegion(tex, x * w, y * h, w, h);
    }

    public TextureRegion getTile(String res) {
        Texture tex = getTexture(res);

        return new TextureRegion(tex);
    }

    private Texture getTexture(String res) {
        if (!assetManager.isLoaded(res, Texture.class)) {
            assetManager.load(res, Texture.class);
            assetManager.finishLoadingAsset(res);
        }

        Texture tex = assetManager.get(res, Texture.class);

        return tex;
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        pixmap.dispose();
        texture.dispose();
    }
}
