package com.gobs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.gobs.StateManager;
import com.gobs.assets.TileFactory;
import com.gobs.components.Designation;
import com.gobs.components.Hidden;
import com.gobs.components.Pending;
import com.gobs.components.Position;
import com.gobs.components.Sprite;
import com.gobs.display.MapDisplay;
import com.gobs.map.TiledMapView;
import com.gobs.map.WorldMap;

public class MapRenderingSystem extends IteratingSystem implements Disposable {
    private ComponentMapper<Position> pm;
    private ComponentMapper<Sprite> sm;
    private ComponentMapper<Designation> dm;

    @Wire
    private TileFactory tileManager;
    @Wire(name = "batch")
    private Batch batch;
    @Wire
    private WorldMap worldMap;
    @Wire
    private StateManager stateManager;

    private MapDisplay display;
    private OrthogonalTiledMapRenderer renderer;
    private TiledMapView mapView;

    private EntitySubscription designations;

    private TextureRegion designationTexture;

    public MapRenderingSystem(MapDisplay display) {
        super(Aspect.all(Position.class, Sprite.class).exclude(Hidden.class));

        this.display = display;
    }

    @Override
    protected void initialize() {
        this.mapView = new TiledMapView(tileManager, worldMap.getWorldWidth(), worldMap.getWorldHeight(), display.getTileSize());

        TiledMap map = mapView.getMap();

        // Scale world coordinates to pixel coordinates
        renderer = new OrthogonalTiledMapRenderer(map, 1.0f / display.getTileSize(), batch);
        renderer.setView(display.getCamera());

        designationTexture = tileManager.getTransparentTile(Color.YELLOW);

        designations = getWorld().getAspectSubscriptionManager().get(Aspect.all(Designation.class, Pending.class));
    }

    @Override
    public void begin() {
        // draw the tile map
        if (worldMap.getCurrentLevel().isDirty()) {
            mapView.drawLayer(worldMap.getCurrentLevel());
        }
        display.getCamera().update();
        renderer.setView(display.getCamera());
        renderer.render();

        // draw entities
        batch.begin();
    }

    @Override
    protected void end() {
        if (stateManager.getState() == StateManager.State.EDITMAP) {
            drawDesignation();
        }

        batch.end();
    }

    public void drawDesignation() {
        for (int i = 0; i < designations.getEntities().size(); i++) {
            int entityId = designations.getEntities().get(i);

            Designation design = dm.get(entityId);

            int x = design.getX();
            int y = design.getY();
            int width = design.getWidth();
            int height = design.getHeight();

            x = Math.min(x, x + width + 1);
            y = Math.min(y, y + height + 1);
            width = Math.abs(width);
            height = Math.abs(height);

            batch.draw(designationTexture, x, y, width, height);
        }
    }

    @Override
    protected void process(int entityId) {
        TextureRegion img = sm.get(entityId).getTexture();
        Position pos = pm.get(entityId);

        batch.draw(img, pos.getX() + pos.getDX(), pos.getY() + pos.getDY(), 1, 1);
    }

    @Override
    public boolean checkProcessing() {
        return stateManager.getState() != StateManager.State.CRAWL && super.checkProcessing();
        //return ((GobsEngine) getEngine()).isRendering() && super.checkProcessing();
    }

    @Override
    public void dispose() {
        mapView.dispose();
        renderer.dispose();
    }
}
