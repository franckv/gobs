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
import com.badlogic.gdx.utils.Disposable;
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
import com.gobs.ui.GUILayout;
import com.gobs.ui.GdxGUI;

public class UIRenderingSystem extends EntitySystem implements Disposable {
    private final ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
    private final ComponentMapper<Controller> cm = ComponentMapper.getFor(Controller.class);
    private final ComponentMapper<Name> nm = ComponentMapper.getFor(Name.class);
    private final ComponentMapper<Party> am = ComponentMapper.getFor(Party.class);
    private final ComponentMapper<HP> hm = ComponentMapper.getFor(HP.class);
    private final ComponentMapper<MP> mm = ComponentMapper.getFor(MP.class);
    private final ComponentMapper<Hidden> dm = ComponentMapper.getFor(Hidden.class);

    private OrthographicDisplay display;
    private StateManager stateManager;
    private Batch batch;
    private GdxGUI gui;

    private Family controllables;
    private Family characters;
    private ImmutableArray<Entity> controllablesEntities;
    private ImmutableArray<Entity> charactersEntities;

    private int margin, spacing;

    public UIRenderingSystem(OrthographicDisplay display, TileFactory tileManager, FontFactory fontManager, StateManager stateManager, Batch batch) {
        this(display, tileManager, fontManager, stateManager, batch, 0);
    }

    public UIRenderingSystem(OrthographicDisplay display, TileFactory tileManager, FontFactory fontManager, StateManager stateManager, Batch batch, int priority) {
        super(priority);

        controllables = Family.all(Position.class, Controller.class).get();
        characters = Family.all(Party.class, Name.class, HP.class, MP.class).get();

        this.display = display;
        this.stateManager = stateManager;
        this.batch = batch;

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

        gui.end();

        batch.end();

        //gui.showCenters();
    }

    private void drawStatusBar() {
        gui.setFont("small");
        gui.setColor(Color.GREEN);

        gui.createSection("PushToBottom", GUILayout.FlowDirection.VERTICAL);

        String msg = "FPS: " + Gdx.graphics.getFramesPerSecond() + " / Entities: " + getEngine().getEntities().size();

        gui.pushToEnd(gui.getLabelHeight(msg));

        gui.createSection("StatusBar", GUILayout.FlowDirection.HORIZONTAL);

        gui.Label(msg);

        // display player position
        for (Entity entity : controllablesEntities) {
            if (dm.get(entity) != null) {
                continue;
            }
            Controller controller = cm.get(entity);
            if (controller == null || !controller.isActive()) {
                continue;
            }

            Position pos = pm.get(entity);

            int x = pos.getX();
            int y = pos.getY();

            msg = "Position: " + x + "," + y;

            gui.pushToEnd(gui.getLabelWidth(msg));

            gui.Label(msg);

            break;
        }

        gui.endSection();

        gui.endSection();
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
                    gui.createSection("Portrait", GUILayout.FlowDirection.NONE);
                    gui.Frame(boxW, boxH);

                    gui.createSection("Details", GUILayout.FlowDirection.VERTICAL);
                    {
                        gui.setMargin(20, 15);
                        gui.setSpacing(10);

                        gui.setColor(Color.WHITE);
                        gui.setFont("large");
                        gui.Label(nm.get(e).getName());

                        gui.Spacer(5);

                        // HP
                        gui.setFont("medium");
                        int hp = hm.get(e).getHP();
                        int maxHP = hm.get(e).getMaxHP();
                        if (hp == maxHP) {
                            gui.setColor(Color.WHITE);
                        } else if (hp < maxHP / 4) {
                            gui.setColor(Color.RED);
                        } else {
                            gui.setColor(Color.GOLD);
                        }
                        gui.Label(String.format("HP: %d / %d", hp, maxHP));

                        // MP
                        gui.setColor(Color.WHITE);
                        int mp = mm.get(e).getMP();
                        int maxMP = mm.get(e).getMaxMP();
                        if (mp == maxMP) {
                            gui.setColor(Color.WHITE);
                        } else if (mp < maxMP / 4) {
                            gui.setColor(Color.RED);
                        } else {
                            gui.setColor(Color.GOLD);
                        }
                        gui.Label(String.format("MP: %d / %d", mp, maxMP));

                        // LVL
                        gui.setColor(Color.WHITE);
                        gui.Label("LV: 99");
                    }
                    gui.endSection();

                    gui.endSection();
                }
            }
        }

        gui.endSection();
    }

    private void drawInventory() {
        gui.createSection("inventory", GUILayout.FlowDirection.NONE);
        {
            gui.Frame(400, 400);

            gui.createSection("", GUILayout.FlowDirection.HORIZONTAL);
            {
                gui.setMargin(50);

                gui.createSection("", GUILayout.FlowDirection.VERTICAL);
                {

                    for (int i = 0; i < 4; i++) {
                        gui.createSection("", GUILayout.FlowDirection.HORIZONTAL);
                        gui.Box("Item" + (2 * i), 50, 50);
                        gui.Box("Item" + (2 * i + 1), 50, 50);
                        gui.endSection();
                    }
                }
                gui.endSection();

                gui.createSection("", GUILayout.FlowDirection.VERTICAL);
                {
                    gui.Spacer(150);
                }
                gui.endSection();

                gui.createSection("", GUILayout.FlowDirection.VERTICAL);
                {
                    gui.Box("Equip1", 50, 50);
                    gui.Box("Equip2", 50, 50);
                    gui.Box("Equip3", 50, 50);
                    gui.Box("Equip4", 50, 50);
                }
                gui.endSection();
            }
            gui.endSection();
        }
        gui.endSection();
    }

    @Override
    public void dispose() {
    }
}
