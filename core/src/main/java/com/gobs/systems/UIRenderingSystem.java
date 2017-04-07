package com.gobs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Disposable;
import com.gobs.GobsEngine;
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
import com.gobs.ui.GUI;
import com.gobs.ui.GUILayout;
import com.gobs.ui.GdxGUI;
import com.gobs.ui.JsonGUILoader;
import java.util.HashMap;
import java.util.Map;

public class UIRenderingSystem extends EntitySystem implements Disposable {
    private final ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
    private final ComponentMapper<Controller> cm = ComponentMapper.getFor(Controller.class);
    private final ComponentMapper<Name> nm = ComponentMapper.getFor(Name.class);
    private final ComponentMapper<Party> am = ComponentMapper.getFor(Party.class);
    private final ComponentMapper<HP> hm = ComponentMapper.getFor(HP.class);
    private final ComponentMapper<MP> mm = ComponentMapper.getFor(MP.class);

    private OrthographicDisplay display;
    private StateManager stateManager;
    private FontFactory fontManager;
    private Batch batch;
    private GUI<Color, BitmapFont> gui;

    private Family controllables;
    private Family characters;
    private ImmutableArray<Entity> controllablesEntities;
    private ImmutableArray<Entity> charactersEntities;

    private int margin, spacing;

    public UIRenderingSystem(OrthographicDisplay display, TileFactory tileManager, StateManager stateManager, Batch batch) {
        this(display, tileManager, stateManager, batch, 0);
    }

    public UIRenderingSystem(OrthographicDisplay display, TileFactory tileManager, StateManager stateManager, Batch batch, int priority) {
        super(priority);

        controllables = Family.all(Position.class, Controller.class).exclude(Hidden.class).get();
        characters = Family.all(Party.class, Name.class, HP.class, MP.class).get();

        this.display = display;
        this.stateManager = stateManager;
        this.batch = batch;

        fontManager = new FontFactory();

        gui = new GdxGUI(display, tileManager, batch);

        gui.addFont("small", fontManager.getFont(16));
        gui.addFont("medium", fontManager.getFont(24));
        gui.addFont("large", fontManager.getFont(30));

        margin = 5;
        spacing = 30;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        controllablesEntities = engine.getEntitiesFor(controllables);
        charactersEntities = engine.getEntitiesFor(characters);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        controllablesEntities = null;
        charactersEntities = null;
    }

    @Override
    public void update(float deltaTime) {
        // overlay text is displayed using absolute screen coordinates to avoid scaling font
        display.getCamera().update();
        batch.setProjectionMatrix(display.getCamera().combined);

        batch.begin();

        gui.begin();

        drawGUI();

        gui.end();

        batch.end();

        //gui.showCenters();
    }

    @Override
    public boolean checkProcessing() {
        return ((GobsEngine) getEngine()).isRendering() && super.checkProcessing();
    }

    private void drawGUI() {
        gui.createSection("Screen", GUILayout.FlowDirection.NONE);

        gui.setMargin(margin);
        gui.setSpacing(spacing);

        drawStatusBar();

        gui.createSection("ui", GUILayout.FlowDirection.VERTICAL);
        if (stateManager.getState() == State.CRAWL) {
            drawCharactersStats();
            //drawInventory();
        }
        gui.endSection();

        gui.endSection();
    }

    private void drawStatusBar() {
        Map<String, String> resolver = new HashMap<String, String>();

        String msg = "FPS: " + Gdx.graphics.getFramesPerSecond() + " / Entities: " + getEngine().getEntities().size();
        resolver.put("$status1", msg);

        Position pos = getPlayerPosition();
        msg = "Position: " + pos.getX() + "," + pos.getY();
        resolver.put("$status2", msg);

        gui.load("ui.json", "statusbar", resolver);
    }

    private Position getPlayerPosition() {
        Position pos = null;

        // display player position
        for (Entity entity : controllablesEntities) {
            Controller controller = cm.get(entity);
            if (controller == null || !controller.isActive()) {
                continue;
            }

            pos = pm.get(entity);

            break;
        }

        return pos;
    }

    private void drawCharactersStats() {
        int nPlayers = charactersEntities.size();
        int boxW = 250;
        int boxH = 150;

        float size = nPlayers * boxW + (nPlayers - 1) * spacing + 2 * margin;
        float space = (display.getViewPort().getWorldWidth() - size) / 2;

        gui.createSection("Portraits", GUILayout.FlowDirection.HORIZONTAL);

        gui.Spacer(space);

        for (int i = 0; i < nPlayers; i++) {
            for (Entity e : charactersEntities) {
                if (am.get(e).getPos() == i + 1) {
                    Map<String, String> resolver = new HashMap<String, String>();

                    resolver.put("$name", nm.get(e).getName());

                    int hp = hm.get(e).getHP();
                    int maxHP = hm.get(e).getMaxHP();
                    String hpColor;

                    if (hp == maxHP) {
                        hpColor = "white";
                    } else if (hp < maxHP / 4) {
                        hpColor = "red";
                    } else {
                        hpColor = "gold";
                    }
                    resolver.put("${hpColor}", hpColor);
                    String hpLabel = String.format("HP: %d / %d", hp, maxHP);
                    resolver.put("$hp", hpLabel);

                    int mp = mm.get(e).getMP();
                    int maxMP = mm.get(e).getMaxMP();
                    String mpColor;

                    if (mp == maxMP) {
                        mpColor = "white";
                    } else if (mp < maxMP / 4) {
                        mpColor = "red";
                    } else {
                        mpColor = "gold";
                    }
                    resolver.put("${mpColor}", mpColor);
                    String mpLabel = String.format("MP: %d / %d", mp, maxMP);
                    resolver.put("$mp", mpLabel);

                    resolver.put("$lvl", "LV: 99");

                    gui.load("ui.json", "character", resolver);
                }
            }
        }

        gui.endSection();
    }

    private void drawInventory() {
        gui.load("ui.json", "inventory", new HashMap<String, String>());
    }

    @Override
    public void dispose() {
        fontManager.dispose();
    }
}
