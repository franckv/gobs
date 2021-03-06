package com.gobs;

import com.artemis.BaseSystem;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.gobs.assets.DungeonFactory;
import com.gobs.assets.TileFactory;
import com.gobs.display.DisplayManager;
import com.gobs.input.ContextManager;
import com.gobs.input.InputHandler;
import com.gobs.map.Level;
import com.gobs.map.WorldMap;
import com.gobs.screens.MainScreen;
import com.gobs.systems.AILoaderSystem;
import com.gobs.systems.AISystem;
import com.gobs.systems.AnimationSystem;
import com.gobs.systems.AssetSystem;
import com.gobs.systems.CameraSystem;
import com.gobs.systems.CollisionSystem;
import com.gobs.systems.ControllerSystem;
import com.gobs.systems.DesignationSystem;
import com.gobs.systems.FPVRenderingSystem;
import com.gobs.systems.InputSystem;
import com.gobs.systems.MapRenderingSystem;
import com.gobs.systems.MapUpdateSystem;
import com.gobs.systems.MovementSystem;
import com.gobs.systems.ProgressSystem;
import com.gobs.systems.TransformationSystem;
import com.gobs.systems.UIRenderingSystem;
import com.gobs.systems.WorkSystem;
import com.gobs.input.InputMap.Input;
import com.gobs.util.CollisionManager;
import java.io.IOException;
import java.io.InputStream;

public class GobsGame extends Game {
    public enum SCREEN {
        WORLD
    }

    private ObjectMap<SCREEN, Screen> screens;
    private SCREEN currentScreen;

    private Config config;
    private World world;

    private InputHandler inputHandler;
    private TileFactory tileManager;
    private DisplayManager displayManager;
    private ContextManager contextManager;
    private CollisionManager<Integer> collisionManager;
    private WorldMap worldMap;
    private StateManager stateManager;
    private Batch batch;

    @Override
    public void create() {
        config = new Config("config.properties");

        inputHandler = new InputHandler(config.getKeyDelay(), config.getKeyRepeat());
        Gdx.input.setInputProcessor(inputHandler);

        tileManager = new TileFactory(config);
        contextManager = new ContextManager();
        displayManager = new DisplayManager(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), config.getTileSize(), 1f);
        collisionManager = new CollisionManager<>(config.getWorldWidth(), config.getWorldHeight());
        batch = new SpriteBatch();

        worldMap = loadWorldMap();

        stateManager = new StateManager(contextManager, StateManager.State.CRAWL);

        initWorld();

        screens = new ObjectMap<>();
        screens.put(SCREEN.WORLD, new MainScreen(displayManager, world));
        currentScreen = SCREEN.WORLD;
        super.setScreen(screens.get(currentScreen));

        loadEntities();

        setKeyBindings();

        Gdx.app.setLogLevel(Application.LOG_INFO);
    }

    @Override
    public void dispose() {
        super.dispose();

        for (Screen s : screens.values()) {
            s.dispose();
        }
        tileManager.dispose();
        batch.dispose();
        for (BaseSystem system : world.getSystems()) {
            if (system instanceof Disposable) {
                ((Disposable) system).dispose();
            }
        }
    }

    private void initWorld() {
        float logicalStep = 1.0f / config.getFPS();

        WorldConfiguration worldConfig = new WorldConfigurationBuilder()
                .with(
                        new WorldSerializationManager(),
                        // logic systems
                        new AssetSystem(),
                        new AILoaderSystem(),
                        new InputSystem(),
                        new ControllerSystem(),
                        new MapUpdateSystem(),
                        new DesignationSystem(),
                        new WorkSystem(),
                        new AISystem(0.5f),
                        new MovementSystem(config.getFPS()),
                        new CollisionSystem(),
                        new AnimationSystem(),
                        new ProgressSystem(),
                        new TransformationSystem(),
                        new CameraSystem(),
                        // rendering systems
                        new FPVRenderingSystem(displayManager.getFPVDisplay()),
                        new MapRenderingSystem(displayManager.getMapDisplay()),
                        new UIRenderingSystem(displayManager.getOverlayDisplay())
                )
                .register(new MainLoopStrategy(logicalStep, config.getPerfMonitor()))
                .build()
                .register(inputHandler)
                .register(tileManager)
                .register(stateManager)
                .register(displayManager)
                .register(collisionManager)
                .register(contextManager)
                .register("batch", batch)
                .register(worldMap);

        world = new World(worldConfig);

        world.getSystem(WorldSerializationManager.class).setSerializer(new JsonArtemisSerializer(world).prettyPrint(true));
    }

    private WorldMap loadWorldMap() {
        WorldMap map = new WorldMap(config.getWorldWidth(), config.getWorldHeight());

        try {
            Level layer = DungeonFactory.loadMap(config.getWorldWidth(), config.getWorldHeight(), "dungeon.map");

            map.addLayer(layer);
        } catch (IOException ex) {
            Gdx.app.error("MAP", "Invalid map file");
            Gdx.app.exit();
        }

        return map;
    }

    private void loadEntities() {
        InputStream is = Gdx.files.internal("entities.json").read();

        world.getSystem(WorldSerializationManager.class).load(is, SaveFileFormat.class);
    }

    private void setKeyBindings() {
        contextManager.mapInput(ContextManager.ContextType.GLOBAL, Input.ESCAPE, ContextManager.Action.EXIT);
        contextManager.mapInput(ContextManager.ContextType.GLOBAL, Input.E, ContextManager.Action.DUMP);
        contextManager.mapInput(ContextManager.ContextType.GLOBAL, Input.B, ContextManager.Action.DEBUG);

        contextManager.mapInput(ContextManager.ContextType.CRAWLING, Input.LEFT, ContextManager.Action.MOVE_LEFT);
        contextManager.mapInput(ContextManager.ContextType.CRAWLING, Input.RIGHT, ContextManager.Action.MOVE_RIGHT);
        contextManager.mapInput(ContextManager.ContextType.CRAWLING, Input.UP, ContextManager.Action.MOVE_UP);
        contextManager.mapInput(ContextManager.ContextType.CRAWLING, Input.DOWN, ContextManager.Action.MOVE_DOWN);
        contextManager.mapInput(ContextManager.ContextType.CRAWLING, Input.TAB, ContextManager.Action.TOGGLE_VIEW);
        contextManager.mapInput(ContextManager.ContextType.CRAWLING, Input.I, ContextManager.Action.INVENTORY);

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
        contextManager.mapInput(ContextManager.ContextType.EDITMAP, Input.T, ContextManager.Action.TARGET);
        contextManager.mapInput(ContextManager.ContextType.EDITMAP, Input.SPACE, ContextManager.Action.TOGGLE_EDIT);
        contextManager.mapInput(ContextManager.ContextType.EDITMAP, Input.LEFT, ContextManager.Action.MOVE_LEFT);
        contextManager.mapInput(ContextManager.ContextType.EDITMAP, Input.RIGHT, ContextManager.Action.MOVE_RIGHT);
        contextManager.mapInput(ContextManager.ContextType.EDITMAP, Input.UP, ContextManager.Action.MOVE_UP);
        contextManager.mapInput(ContextManager.ContextType.EDITMAP, Input.DOWN, ContextManager.Action.MOVE_DOWN);
        contextManager.mapInput(ContextManager.ContextType.EDITMAP, Input.TAB, ContextManager.Action.TOGGLE_VIEW);
        contextManager.mapInput(ContextManager.ContextType.EDITMAP, Input.ENTER, ContextManager.Action.COMPLETE);

        contextManager.activateContext(ContextManager.ContextType.CRAWLING);
        contextManager.activateContext(ContextManager.ContextType.GLOBAL);
    }
}
