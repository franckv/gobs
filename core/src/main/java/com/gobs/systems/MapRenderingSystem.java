package com.gobs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.gobs.GobsEngine;
import com.gobs.assets.TileFactory;
import com.gobs.components.Hidden;
import com.gobs.components.Position;
import com.gobs.components.Sprite;
import com.gobs.display.MapDisplay;
import com.gobs.map.TiledMapView;
import com.gobs.map.WorldMap;

public class MapRenderingSystem extends IteratingSystem implements Disposable {
    private final ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
    private final ComponentMapper<Sprite> sm = ComponentMapper.getFor(Sprite.class);

    private MapDisplay display;
    private OrthogonalTiledMapRenderer renderer;
    private Batch batch;
    private WorldMap worldMap;
    private TiledMapView mapView;

    public MapRenderingSystem(MapDisplay display, TileFactory tileManager, WorldMap worldMap, Batch batch) {
        this(display, tileManager, worldMap, batch, 0);
    }

    public MapRenderingSystem(MapDisplay display, TileFactory tileManager, WorldMap worldMap, Batch batch, int priority) {
        super(Family.all(Position.class, Sprite.class).exclude(Hidden.class).get(), priority);

        this.display = display;
        this.worldMap = worldMap;
        this.batch = batch;
        this.mapView = new TiledMapView(tileManager, worldMap.getWorldWidth(), worldMap.getWorldHeight(), display.getTileSize());

        TiledMap map = mapView.getMap();

        // Scale world coordinates to pixel coordinates
        renderer = new OrthogonalTiledMapRenderer(map, 1.0f / display.getTileSize(), batch);
        renderer.setView(display.getCamera());
    }

    @Override
    public void update(float deltaTime) {
        // draw the tile map
        if (worldMap.getCurrentLevel().isDirty()) {
            mapView.drawLayer(worldMap.getCurrentLevel());
        }
        display.getCamera().update();
        renderer.setView(display.getCamera());
        renderer.render();

        // draw entities
        batch.begin();

        super.update(deltaTime);

        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TextureRegion img = sm.get(entity).getTexture();
        Position pos = pm.get(entity);

        batch.draw(img, pos.getX() + pos.getDX(), pos.getY() + pos.getDY(), 1, 1);
    }

    @Override
    public boolean checkProcessing() {
        return ((GobsEngine) getEngine()).isRendering() && super.checkProcessing();
    }

    @Override
    public void dispose() {
        mapView.dispose();
        renderer.dispose();
    }
}
