package com.gobs;

import com.gobs.assets.EntityFactory;
import com.gobs.assets.DungeonFactory;
import com.gobs.map.Layer;
import com.gobs.map.TiledMapView;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gobs.assets.FontFactory;
import com.gobs.assets.TileFactory;
import com.gobs.input.ContextManager;
import com.gobs.input.InputHandler;
import com.gobs.managers.CollisionManager;
import com.gobs.screens.MainScreen;
import com.gobs.screens.MapScreen;
import com.gobs.systems.AISystem;
import com.gobs.systems.CollisionSystem;
import com.gobs.systems.FPVRenderingSystem;
import com.gobs.systems.InputSystem;
import com.gobs.systems.MapRenderingSystem;
import com.gobs.systems.MovementSystem;
import com.gobs.systems.TransformationSystem;
import com.gobs.systems.UIRenderingSystem;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState implements Disposable {
    public enum SCREEN {
        WORLD, INVENTORY, MAP
    }

    private Game game;

    private static GameState currentState = null;

    private Config config;
    private Engine engine;
    private InputHandler inputHandler;

    private Map<SCREEN, Screen> screens;
    private SCREEN screen;

    // camera to display map items
    private OrthographicCamera mapCamera;
    // camera to display overlay text
    private OrthographicCamera overlayCamera;
    // camera for FPV
    private PerspectiveCamera fpvCamera;

    private Viewport mapViewPort;
    private Viewport overlayViewPort;
    private Viewport fpvViewPort;

    private TileFactory tileManager;
    private FontFactory fontManager;
    private ContextManager contextManager;
    private CollisionManager collisionManager;
    private TiledMapView mapView;
    private Layer mapLayer;
    private Batch batch;

    private AssetManager assetManager;

    private int screenWidth;
    private int screenHeight;
    private float screenRatio;
    private int tileSize;
    private int mapWidth;
    private int mapHeight;
    private int worldWidth;
    private int worldHeight;

    private GameState() {
    }

    public void init() {
        config = new Config("config.properties");
        engine = new Engine();

        // screen resolution
        // TODO: update when resizing
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        screenRatio = (float) screenWidth / screenHeight;

        tileSize = config.getTileSize();

        // size of the whole map
        worldWidth = config.getWordWidth();
        worldHeight = config.getWorldHeight();

        // part of the map visible on screen
        mapWidth = (int) screenWidth / tileSize;
        mapHeight = (int) screenHeight / tileSize;

        inputHandler = new InputHandler();
        tileManager = new TileFactory(tileSize);
        fontManager = new FontFactory();
        contextManager = new ContextManager();
        collisionManager = new CollisionManager(worldWidth, worldHeight);
        mapView = new TiledMapView(worldWidth, worldHeight, tileSize);
        batch = new SpriteBatch();

        assetManager = new AssetManager();

        screens = new HashMap<>();
        screens.put(SCREEN.WORLD, new MainScreen());
        screens.put(SCREEN.MAP, new MapScreen());

        initCamera();
        initSystems();

        loadMap();
        loadEntities();
    }

    public static GameState getGameState() {
        if (currentState == null) {
            currentState = new GameState();
            currentState.init();
        }
        return currentState;
    }

    public void initCamera() {
        mapCamera = new OrthographicCamera();
        mapCamera.setToOrtho(false, mapWidth, mapHeight);

        mapViewPort = new FitViewport(mapWidth, mapHeight, mapCamera);
        mapViewPort.apply();

        overlayCamera = new OrthographicCamera();
        overlayCamera.setToOrtho(false, screenWidth, screenHeight);

        overlayViewPort = new FitViewport(screenWidth, screenHeight, overlayCamera);
        overlayViewPort.apply();

        fpvCamera = new PerspectiveCamera(67, screenWidth, screenHeight);
        fpvCamera.near = 0.1f;
        fpvCamera.far = 200f;
        fpvCamera.up.set(0, 0, 1);

        fpvViewPort = new FitViewport(screenWidth, screenHeight, fpvCamera);
        fpvViewPort.apply();
    }

    public void initSystems() {
        engine.addSystem(new FPVRenderingSystem());
        engine.addSystem(new MapRenderingSystem(batch));
        engine.addSystem(new UIRenderingSystem(batch));
        engine.addSystem(new InputSystem());
        engine.addSystem(new AISystem(0.5f));
        engine.addSystem(new MovementSystem());
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new TransformationSystem());
    }

    public void loadMap() {
        try {
            mapLayer = DungeonFactory.loadMap("dungeon.map");
        } catch (IOException ex) {
            Gdx.app.error("MAP", "Invalid map file");
            Gdx.app.exit();
        }
    }

    public void loadEntities() {
        List<Entity> entities = EntityFactory.loadEntities("entities.json");

        for (Entity entity : entities) {
            engine.addEntity(entity);
        }
    }

    public static Config getConfig() {
        return getGameState().config;
    }

    public static Engine getEngine() {
        return getGameState().engine;
    }

    public static int getMapHeight() {
        return getGameState().mapHeight;
    }

    public static int getMapWidth() {
        return getGameState().mapWidth;
    }

    public static float getScreenRatio() {
        return getGameState().screenRatio;
    }

    public static int getWorldHeight() {
        return getGameState().worldHeight;
    }

    public static int getWorldWidth() {
        return getGameState().worldWidth;
    }

    public static int getTileSize() {
        return getGameState().tileSize;
    }

    public static InputHandler getInputHandler() {
        return getGameState().inputHandler;
    }

    public SCREEN getScreen() {
        return screen;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setScreen(SCREEN screenType) {
        screen = screenType;
        game.setScreen(screens.get(screenType));
    }

    public static Viewport getMapViewport() {
        return getGameState().mapViewPort;
    }

    public static Viewport getOverlayViewport() {
        return getGameState().overlayViewPort;
    }

    public static Viewport getFPVViewport() {
        return getGameState().fpvViewPort;
    }

    public static OrthographicCamera getMapCamera() {
        return getGameState().mapCamera;
    }

    public static OrthographicCamera getOverlayCamera() {
        return getGameState().overlayCamera;
    }

    public static PerspectiveCamera getFPVCamera() {
        return getGameState().fpvCamera;
    }

    public static TileFactory getTileManager() {
        return getGameState().tileManager;
    }

    public static FontFactory getFontManager() {
        return getGameState().fontManager;
    }

    public static TiledMapView getMapView() {
        return getGameState().mapView;
    }

    public static Layer getMapLayer() {
        return getGameState().mapLayer;
    }

    public static CollisionManager getCollisionManager() {
        return getGameState().collisionManager;
    }

    public static ContextManager getContextManager() {
        return getGameState().contextManager;
    }
    
    public static AssetManager getAssetManager() {
        return getGameState().assetManager;
    }

    @Override
    public void dispose() {
        for (Screen s : screens.values()) {
            s.dispose();
        }
        mapView.dispose();
        tileManager.dispose();
        batch.dispose();
        assetManager.dispose();
        for (EntitySystem system : engine.getSystems()) {
            if (system instanceof Disposable) {
                ((Disposable) system).dispose();
            }
        }
    }
}
