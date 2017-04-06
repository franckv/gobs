package com.gobs.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Disposable;
import com.gobs.assets.TileFactory;

public class TiledMapView implements Disposable {
    private TiledMap map;
    private TileFactory tileManager;

    private TiledMapTile floorTile;
    private TiledMapTile stairsTile;
    private TiledMapTile wallTile;

    public TiledMapView(TileFactory tileManager, int width, int height, int tileSize) {
        this.tileManager = tileManager;
        map = new TiledMap();

        map.getLayers().add(new TiledMapTileLayer(width, height, tileSize, tileSize));

        floorTile = new StaticTiledMapTile(tileManager.getFullTile(Color.LIGHT_GRAY));
        stairsTile = new StaticTiledMapTile(tileManager.getFullTile(Color.BLUE));
        wallTile = new StaticTiledMapTile(tileManager.getFullTile(Color.CLEAR));
    }

    public TiledMap getMap() {
        return map;
    }

    public void drawLayer(Level layer) {
        for (LevelCell c : layer) {
            if (c != null) {
                TiledMapTile tile = null;

                switch (c.getType()) {
                    case STAIRS:
                        tile = stairsTile;
                        break;
                    case FLOOR:
                        tile = floorTile;
                        break;
                    case WALL:
                        tile = wallTile;
                        break;
                }
                paintCell(c.getX(), c.getY(), tile);
            }
        }
    }

    private void paintCell(int x, int y, TiledMapTile tile) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);

        Cell cell = layer.getCell(x, y);

        if (cell == null) {
            cell = new Cell();
            layer.setCell(x, y, cell);
        }

        cell.setTile(tile);
    }

    @Override
    public void dispose() {
        map.dispose();
    }
}
