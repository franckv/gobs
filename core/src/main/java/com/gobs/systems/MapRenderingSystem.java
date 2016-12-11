package com.gobs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.gobs.GameState;
import com.gobs.components.Hidden;
import com.gobs.components.Position;
import com.gobs.components.Sprite;

public class MapRenderingSystem extends EntityProcessingSystem {
    private final ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
    private final ComponentMapper<Sprite> sm = ComponentMapper.getFor(Sprite.class);

    OrthogonalTiledMapRenderer renderer;
    Batch batch;

    public MapRenderingSystem(Batch batch) {
        this(batch, 0);
    }

    public MapRenderingSystem(Batch batch, int priority) {
        super(Family.all(Position.class, Sprite.class).exclude(Hidden.class).get(), priority);

        this.batch = batch;

        TiledMap map = GameState.getMapView().getMap();

        // Scale world coordinates to pixel coordinates
        renderer = new OrthogonalTiledMapRenderer(map, 1.0f / GameState.getTileSize(), batch);
        renderer.setView(GameState.getMapCamera());
    }

    @Override
    public void update(float deltaTime) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);

        // draw the tile map
        GameState.getMapView().drawLayer(GameState.getMapLayer());
        GameState.getMapCamera().update();
        renderer.setView(GameState.getMapCamera());
        renderer.render();

        // draw entities
        batch.begin();

        for (Entity entity : getEntities()) {
            renderEntity(entity);
        }

        batch.end();
    }

    private void renderEntity(Entity entity) {
        TextureRegion img = sm.get(entity).getTexture();
        Position pos = pm.get(entity);

        batch.draw(img, pos.getX(), pos.getY(), 1, 1);
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }
}
