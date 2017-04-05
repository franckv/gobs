package com.gobs.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Disposable;
import com.gobs.assets.TileFactory;
import com.gobs.map.Layer.LayerType;

/**
 *
 */
public class TiledMapView implements Disposable {
    private TiledMap map;
    private TileFactory tileManager;

    public TiledMapView(TileFactory tileManager, int width, int height, int tileSize) {
        this.tileManager = tileManager;
        map = new TiledMap();

        for (int i=0;i<LayerType.values().length;i++) {
            map.getLayers().add(new TiledMapTileLayer(width, height, tileSize, tileSize));
        }
    }

    public TiledMap getMap() {
        return map;
    }

    public void drawLayer(Layer layer) {
        for (LayerCell c : layer) {
            if (c != null) {
                Color color = null;
                boolean isFilled = false;
                switch (c.getType()) {
                    case STAIRS:
                        color = Color.BLUE;
                        isFilled = true;
                        break;
                    case FLOOR:
                        color = Color.LIGHT_GRAY;
                        isFilled = true;
                        break;
                    case WALL:
                        color = Color.DARK_GRAY;
                        isFilled = true;
                        break;
                }
                paintCell(getCell(c.getX(), c.getY(), layer.getType()), color, isFilled);
            }
        }
    }

    private Cell getCell(int x, int y, LayerType type) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(type.ordinal());

        Cell cell = layer.getCell(x, y);

        if (cell == null) {
            cell = new Cell();
            layer.setCell(x, y, cell);
        }
        return cell;
    }

    private void paintCell(TiledMapTileLayer.Cell cell, Color color, boolean fill) {
        TextureRegion tile;

        if (fill) {
            tile = tileManager.getFullTile(color);
        } else {
            tile = tileManager.getRectTile(color);
        }

        if (cell.getTile() == null) {
            cell.setTile(new StaticTiledMapTile(tile));
        } else {
            cell.getTile().setTextureRegion(tile);
        }
    }

    @Override
    public void dispose() {
        map.dispose();
    }
}
