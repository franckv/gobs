package com.gobs.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.gobs.Config;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private ObjectMap<Color, TextureRegion> fullTiles;
    private ObjectMap<Color, TextureRegion> rectTiles;
    private ObjectMap<Color, TextureRegion> transTiles;

    private static Pattern fileResourcePattern = Pattern.compile("^file:(.*)!(\\d+),(\\d+):(\\d+),(\\d+)$");
    private static Pattern colorResourcePattern = Pattern.compile("^color:(.*)!(\\d+)$");

    public TileFactory(Config config) {
        this.assetManager = new AssetManager();
        this.tileSize = config.getTileSize();

        fullTiles = new ObjectMap<>();
        rectTiles = new ObjectMap<>();
        transTiles = new ObjectMap<>();

        Array<Color> colors = new Array<>();

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

    private void initTiles(Array<Color> colors) {
        pixmap = new Pixmap(colors.size * tileSize, 3 * tileSize, Pixmap.Format.RGBA8888);

        for (int i = 0; i < colors.size; i++) {
            Color color = colors.get(i);

            pixmap.setColor(color);
            pixmap.fillRectangle(i * tileSize, 0, tileSize, tileSize);
            pixmap.drawRectangle(i * tileSize, tileSize, tileSize, tileSize);
            pixmap.setColor(color.a, color.g, color.b, 0.4f);
            pixmap.fillRectangle(i * tileSize, 2 * tileSize, tileSize, tileSize);
        }

        texture = new Texture(pixmap);

        for (int i = 0; i < colors.size; i++) {
            Color color = colors.get(i);
            fullTiles.put(color, new TextureRegion(texture, i * tileSize, 0, tileSize, tileSize));
            rectTiles.put(color, new TextureRegion(texture, i * tileSize, tileSize, tileSize, tileSize));
            transTiles.put(color, new TextureRegion(texture, i * tileSize, 2 * tileSize, tileSize, tileSize));
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

    public TextureRegion getTransparentTile(Color color) {
        if (transTiles.containsKey(color)) {
            return transTiles.get(color);
        } else {
            return transTiles.get(Color.DARK_GRAY);
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

    public TextureRegion resolveTexture(String path) {
        Matcher fileMatcher = fileResourcePattern.matcher(path);
        Matcher colorMatcher = colorResourcePattern.matcher(path);

        if (fileMatcher.matches()) {
            String textureName = fileMatcher.group(1);
            int x = Integer.parseInt(fileMatcher.group(2));
            int y = Integer.parseInt(fileMatcher.group(3));
            int w = Integer.parseInt(fileMatcher.group(4));
            int h = Integer.parseInt(fileMatcher.group(5));

            return getTile(textureName, x, y, w, h);
        } else if (colorMatcher.matches()) {
            String colorName = colorMatcher.group(1);
            int fill = Integer.parseInt(colorMatcher.group(2));

            Color color = Color.BLACK;
            try {
                color = (Color) Color.class.getDeclaredField(colorName).get(null);
            } catch (NoSuchFieldException | SecurityException
                    | IllegalArgumentException | IllegalAccessException ex) {
                Gdx.app.error("JSON", "Invalid color: " + colorName);
            }

            if (fill == 1) {
                return getFullTile(color);
            } else {
                return getRectTile(color);
            }
        } else {
            return getTile(path);
        }
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        pixmap.dispose();
        texture.dispose();
    }
}
