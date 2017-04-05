package com.gobs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.gobs.assets.DungeonFactory;
import com.gobs.assets.EntityFactory;
import com.gobs.assets.FontFactory;
import com.gobs.assets.TileFactory;
import com.gobs.display.DisplayManager;
import com.gobs.input.ContextManager;
import com.gobs.input.InputHandler;
import com.gobs.map.Layer;
import com.gobs.map.TiledMapView;
import com.gobs.screens.MainScreen;
import com.gobs.systems.AISystem;
import com.gobs.systems.CollisionSystem;
import com.gobs.systems.ControllerSystem;
import com.gobs.systems.FPVRenderingSystem;
import com.gobs.systems.InputSystem;
import com.gobs.systems.MapInputSystem;
import com.gobs.systems.MapRenderingSystem;
import com.gobs.systems.MovementSystem;
import com.gobs.systems.TransformationSystem;
import com.gobs.systems.UIRenderingSystem;
import com.gobs.ui.Input;
import com.gobs.util.CollisionManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GobsGame extends Game {
    public enum SCREEN {
        WORLD
    }

    private Map<SCREEN, Screen> screens;
    private SCREEN currentScreen;

    private Config config;
    private GobsEngine engine;

    private InputHandler inputHandler;
    private TileFactory tileManager;
    private FontFactory fontManager;
    private DisplayManager displayManager;
    private ContextManager contextManager;
    private CollisionManager<Entity> collisionManager;
    private TiledMapView mapView;
    private Layer mapLayer;
    private StateManager stateManager;
    private Batch batch;

    @Override
    public void create() {
        inputHandler = new InputHandler();
        Gdx.input.setInputProcessor(inputHandler);

        config = new Config("config.properties");
        engine = new GobsEngine();

        int tileSize = config.getTileSize();

        tileManager = new TileFactory(config, tileSize);
        fontManager = new FontFactory();
        contextManager = new ContextManager();
        displayManager = new DisplayManager(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), config.getWorldWidth(), config.getWorldHeight(), tileSize);
        collisionManager = new CollisionManager<>(config.getWorldWidth(), config.getWorldHeight());
        mapView = new TiledMapView(tileManager, config.getWorldWidth(), config.getWorldHeight(), tileSize);
        batch = new SpriteBatch();

        screens = new HashMap<>();
        screens.put(SCREEN.WORLD, new MainScreen(displayManager, engine));
        currentScreen = SCREEN.WORLD;
        super.setScreen(screens.get(currentScreen));

        loadMap();
        loadEntities(collisionManager, tileManager);
        setKeyBindings();

        stateManager = new StateManager(engine, contextManager, mapLayer, StateManager.State.CRAWL);

        initSystems();

        Gdx.app.setLogLevel(Application.LOG_INFO);
    }

    @Override
    public void dispose() {
        super.dispose();

        for (Screen s : screens.values()) {
            s.dispose();
        }
        mapView.dispose();
        tileManager.dispose();
        fontManager.dispose();
        batch.dispose();
        for (EntitySystem system : engine.getSystems()) {
            if (system instanceof Disposable) {
                ((Disposable) system).dispose();
            }
        }
    }

    public void initSystems() {
        // logic systems
        engine.addSystem(new InputSystem(displayManager.getMapDisplay(), inputHandler, contextManager));
        engine.addSystem(new ControllerSystem(contextManager));
        engine.addSystem(new MapInputSystem(contextManager, stateManager, mapLayer));
        engine.addSystem(new AISystem(0.5f));
        engine.addSystem(new MovementSystem());
        engine.addSystem(new CollisionSystem(collisionManager, config.getWorldWidth(), config.getWorldHeight(), mapLayer));
        engine.addSystem(new TransformationSystem());

        // rendering systems
        engine.addSystem(new FPVRenderingSystem(displayManager.getFPVDisplay(), config.getWorldWidth(), config.getWorldHeight(), mapLayer));
        engine.addSystem(new MapRenderingSystem(displayManager.getMapDisplay(), mapView, mapLayer, batch), false);
        engine.addSystem(new UIRenderingSystem(displayManager.getOverlayDisplay(), tileManager, fontManager, stateManager, batch));
    }

    public void loadMap() {
        try {
            mapLayer = DungeonFactory.loadMap(config.getWorldWidth(), config.getWorldHeight(), "dungeon.map");
        } catch (IOException ex) {
            Gdx.app.error("MAP", "Invalid map file");
            Gdx.app.exit();
        }
    }

    public void loadEntities(CollisionManager<Entity> collisionManager, TileFactory tileManager) {
        List<Entity> entities = (new EntityFactory(collisionManager, tileManager)).loadEntities("entities.json");

        for (Entity entity : entities) {
            engine.addEntity(entity);
        }
    }

    private void setKeyBindings() {
        contextManager.mapInput(ContextManager.ContextType.GLOBAL, Input.ESCAPE, ContextManager.Action.EXIT);
        contextManager.mapInput(ContextManager.ContextType.GLOBAL, Input.E, ContextManager.Action.DUMP);

        contextManager.mapInput(ContextManager.ContextType.CRAWLING, Input.LEFT, ContextManager.Action.MOVE_LEFT);
        contextManager.mapInput(ContextManager.ContextType.CRAWLING, Input.RIGHT, ContextManager.Action.MOVE_RIGHT);
        contextManager.mapInput(ContextManager.ContextType.CRAWLING, Input.UP, ContextManager.Action.MOVE_UP);
        contextManager.mapInput(ContextManager.ContextType.CRAWLING, Input.DOWN, ContextManager.Action.MOVE_DOWN);
        contextManager.mapInput(ContextManager.ContextType.CRAWLING, Input.TAB, ContextManager.Action.TOGGLE_VIEW);

        contextManager.mapInput(ContextManager.ContextType.MAP, Input.Q, ContextManager.Action.SCROLL_LEFT);
        contextManager.mapInput(ContextManager.ContextType.MAP, Input.D, ContextManager.Action.SCROLL_RIGHT);
        contextManager.mapInput(ContextManager.ContextType.MAP, Input.Z, ContextManager.Action.SCROLL_UP);
        contextManager.mapInput(ContextManager.ContextType.MAP, Input.S, ContextManager.Action.SCROLL_DOWN);
        contextManager.mapInput(ContextManager.ContextType.MAP, Input.A, ContextManager.Action.ZOOM_IN);
        contextManager.mapInput(ContextManager.ContextType.MAP, Input.W, ContextManager.Action.ZOOM_OUT);
        contextManager.mapInput(ContextManager.ContextType.MAP, Input.TAB, ContextManager.Action.TOGGLE_VIEW);
        contextManager.mapInput(ContextManager.ContextType.MAP, Input.SPACE, ContextManager.Action.TOGGLE_EDIT);

        contextManager.mapInput(ContextManager.ContextType.EDITMAP, Input.D, ContextManager.Action.DIG);
        contextManager.mapInput(ContextManager.ContextType.EDITMAP, Input.F, ContextManager.Action.FILL);
        contextManager.mapInput(ContextManager.ContextType.EDITMAP, Input.ENTER, ContextManager.Action.TARGET);
        contextManager.mapInput(ContextManager.ContextType.EDITMAP, Input.SPACE, ContextManager.Action.TOGGLE_EDIT);
        contextManager.mapInput(ContextManager.ContextType.EDITMAP, Input.LEFT, ContextManager.Action.MOVE_LEFT);
        contextManager.mapInput(ContextManager.ContextType.EDITMAP, Input.RIGHT, ContextManager.Action.MOVE_RIGHT);
        contextManager.mapInput(ContextManager.ContextType.EDITMAP, Input.UP, ContextManager.Action.MOVE_UP);
        contextManager.mapInput(ContextManager.ContextType.EDITMAP, Input.DOWN, ContextManager.Action.MOVE_DOWN);
        contextManager.mapInput(ContextManager.ContextType.EDITMAP, Input.TAB, ContextManager.Action.TOGGLE_VIEW);

        contextManager.activateContext(ContextManager.ContextType.CRAWLING);
        contextManager.activateContext(ContextManager.ContextType.GLOBAL);
    }
}
