package com.gobs.systems;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.annotations.Wire;
import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Sort;
import com.gobs.MainLoopStrategy;
import com.gobs.StateManager;
import com.gobs.StateManager.State;
import com.gobs.assets.FontFactory;
import com.gobs.assets.TileFactory;
import com.gobs.components.Controller;
import com.gobs.components.HP;
import com.gobs.components.Hidden;
import com.gobs.components.MP;
import com.gobs.components.Name;
import com.gobs.components.Party;
import com.gobs.components.Position;
import com.gobs.display.OrthographicDisplay;
import com.gobs.input.ContextManager;
import com.gobs.input.InputHandler;
import com.gobs.ui.GobsGUI;
import com.gobs.ui.UIState;

public class UIRenderingSystem extends BaseSystem implements Disposable, RenderingSystem {
    private ComponentMapper<Position> pm;
    private ComponentMapper<Controller> cm;
    private ComponentMapper<Name> nm;
    private ComponentMapper<Party> am;
    private ComponentMapper<HP> hm;
    private ComponentMapper<MP> mm;

    @Wire
    private TileFactory tileManager;
    @Wire
    private StateManager stateManager;
    @Wire(name = "batch")
    private Batch batch;
    @Wire
    private InputHandler inputHandler;
    @Wire
    private ContextManager contextManager;

    private EntitySubscription controllables;
    private EntitySubscription characters;
    private EntitySubscription allEntities;

    private OrthographicDisplay display;
    private FontFactory fontManager;
    private GobsGUI gui;

    private UIState state;

    private int margin, spacing;

    private final static String consummerID = UIRenderingSystem.class.getName();

    public UIRenderingSystem(OrthographicDisplay display) {
        this.display = display;

        this.fontManager = new FontFactory();

        margin = 5;
        spacing = 30;
    }

    @Override
    protected void initialize() {
        this.controllables = getWorld().getAspectSubscriptionManager().get(Aspect.all(Position.class, Controller.class).exclude(Hidden.class));
        this.characters = getWorld().getAspectSubscriptionManager().get(Aspect.all(Party.class, Name.class, HP.class, MP.class));
        this.allEntities = getWorld().getAspectSubscriptionManager().get(Aspect.all());

        gui = new GobsGUI(display, tileManager, batch);

        state = new UIState(gui);

        gui.addFont("small", fontManager.getFont(16));
        gui.addFont("medium", fontManager.getFont(24));
        gui.addFont("large", fontManager.getFont(30));

        gui.load(Gdx.files.internal("ui.json").reader());

        registerActions();
    }

    @Override
    protected void processSystem() {
        processInputs();

        // overlay text is displayed using absolute screen coordinates to avoid scaling font
        display.getCamera().update();

        // TODO: mouse inputs may be lost if system processing time is too long
        gui.acceptInput(inputHandler.getInputMap());

        batch.setProjectionMatrix(display.getCamera().combined);

        batch.begin();

        gui.begin();

        updateStatus();

        if (stateManager.getState() == State.CRAWL) {
            updateCharactersStats();
            if (state.getState() == UIState.State.NONE) {
                state.setState(UIState.State.CRAWL);
            }
        } else {
            state.setState(UIState.State.NONE);
        }

        MainLoopStrategy strategy = (MainLoopStrategy) world.getInvocationStrategy();

        if (strategy.hasPerfData()) {
            ObjectMap<String, Long> perfData = strategy.getPerfData();

            Array<String> systems = new Array<>();
            for (String system : perfData.keys()) {
                systems.add(system + ": " + perfData.get(system));
            }

            Sort.instance().sort(systems);

            gui.setListValue("perftable", "values", systems);
        }

        dumpEntities();

        gui.showFragment("ui");

        gui.end();

        batch.end();

        //gui.showCenters();
    }

    private void updateStatus() {
        String msg = "FPS: " + Gdx.graphics.getFramesPerSecond() + " / Entities: " + allEntities.getEntities().size();

        gui.setStringValue("fpsStatus", "label", msg);

        Position pos = getPlayerPosition();
        msg = "Position: " + pos.getX() + "," + pos.getY();

        gui.setStringValue("positionStatus", "label", msg);
    }

    private void updateCharactersStats() {
        int nPlayers = characters.getEntities().size();
        int boxW = 250;

        float size = nPlayers * boxW + (nPlayers - 1) * spacing;
        float space = (display.getViewPort().getWorldWidth() - size - 2 * margin) / 2 - spacing;

        gui.setIntValue("charactersSpacing", "width", (int) space);

        for (int i = 0; i < nPlayers; i++) {
            for (int j = 0; j < characters.getEntities().size(); j++) {
                int character = characters.getEntities().get(j);

                if (am.get(character).getPos() == i + 1) {

                    gui.setStringValue("name." + i, "label", nm.get(character).getName());

                    int hp = hm.get(character).getHP();
                    int maxHP = hm.get(character).getMaxHP();
                    String hpColor;

                    if (hp == maxHP) {
                        hpColor = "white";
                    } else if (hp < maxHP / 4) {
                        hpColor = "red";
                    } else {
                        hpColor = "gold";
                    }

                    gui.setStringValue("hpColor." + i, "color", hpColor);

                    String hpLabel = String.format("HP: %d / %d", hp, maxHP);

                    gui.setStringValue("hp." + i, "label", hpLabel);

                    int mp = mm.get(character).getMP();
                    int maxMP = mm.get(character).getMaxMP();
                    String mpColor;

                    if (mp == maxMP) {
                        mpColor = "white";
                    } else if (mp < maxMP / 4) {
                        mpColor = "red";
                    } else {
                        mpColor = "gold";
                    }

                    gui.setStringValue("mpColor." + i, "color", mpColor);

                    String mpLabel = String.format("MP: %d / %d", mp, maxMP);

                    gui.setStringValue("mp." + i, "label", mpLabel);

                    gui.setStringValue("lvl." + i, "label", "LV: 99");
                }
            }
        }
    }

    private Position getPlayerPosition() {
        Position pos = null;

        // display player position
        for (int i = 0; i < controllables.getEntities().size(); i++) {
            int entityId = controllables.getEntities().get(i);

            Controller controller = cm.get(entityId);
            if (controller == null || !controller.isActive()) {
                continue;
            }

            pos = pm.get(entityId);

            break;
        }

        return pos;
    }

    private void dumpEntities() {
        StringBuilder entities = new StringBuilder();

        boolean firstE = true;

        for (int i = 0; i < allEntities.getEntities().size(); i++) {
            if (!firstE) {
                entities.append("]\n");

            }
            int entityId = allEntities.getEntities().get(i);

            entities.append(entityId);
            entities.append(": [");

            Bag<Component> components = new Bag<>();
            getWorld().getEntity(entityId).getComponents(components);

            boolean firstC = true;
            for (Component component : components) {
                if (!firstC) {
                    entities.append(",");
                }
                entities.append(component.getClass().getSimpleName());
                firstC = false;
            }
            firstE = false;
        }

        gui.setStringValue("entities", "label", entities.toString());
    }

    private void processInputs() {
        Array<ContextManager.Event> events = contextManager.pollActions(consummerID);

        for (ContextManager.Event event : events) {
            switch (event.getAction()) {
                case DEBUG:
                    gui.toggleFragment("statusbar");
                    gui.toggleFragment("debug");
                    break;
                case INVENTORY:
                    if (state.getState() == UIState.State.CRAWL) {
                        state.setState(UIState.State.INVENTORY);
                    } else {
                        state.setState(UIState.State.CRAWL);
                    }
                    break;
            }
        }
    }

    private void registerActions() {
        contextManager.registerConsumer(consummerID, ContextManager.ContextType.GLOBAL, ContextManager.Action.DEBUG);
        contextManager.registerConsumer(consummerID, ContextManager.ContextType.CRAWLING, ContextManager.Action.INVENTORY);
    }

    @Override
    public void dispose() {
        fontManager.dispose();
    }
}
