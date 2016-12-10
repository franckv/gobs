package com.gobs.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.gobs.GameState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory for color tiles
 */
public class TileFactory implements Disposable {
    private int tileSize; // size in pixels
    private Pixmap pixmap;
    private Texture texture;

    private Map<Color, TextureRegion> fullTiles;
    private Map<Color, TextureRegion> rectTiles;

    public TileFactory(int tileSize) {
        this.tileSize = tileSize;

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

        initTiles(colors);
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
        return getTile("sprites/frame.png");
    }
        
    public TextureRegion getFrameSelected() {
        return getTile("sprites/frame_selected.png");
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
        AssetManager manager = GameState.getAssetManager();

        if (!manager.isLoaded(res, Texture.class)) {
            manager.load(res, Texture.class);
            manager.finishLoadingAsset(res);
        }

        Texture tex = manager.get(res, Texture.class);

        return tex;
    }

    @Override
    public void dispose() {
        pixmap.dispose();
        texture.dispose();
    }
}
