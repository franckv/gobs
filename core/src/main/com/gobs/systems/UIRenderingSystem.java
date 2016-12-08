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
import com.gobs.GameState;
import com.gobs.RunningState;
import com.gobs.components.Controller;
import com.gobs.components.HP;
import com.gobs.components.Hidden;
import com.gobs.components.MP;
import com.gobs.components.Name;
import com.gobs.components.Party;
import com.gobs.components.Position;
import com.gobs.ui.GUI;
import com.gobs.ui.GUILayout;

public class UIRenderingSystem extends EntitySystem implements Disposable {
    private final ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
    private final ComponentMapper<Controller> cm = ComponentMapper.getFor(Controller.class);
    private final ComponentMapper<Name> nm = ComponentMapper.getFor(Name.class);
    private final ComponentMapper<Party> am = ComponentMapper.getFor(Party.class);
    private final ComponentMapper<HP> hm = ComponentMapper.getFor(HP.class);
    private final ComponentMapper<MP> mm = ComponentMapper.getFor(MP.class);

    private Batch batch;
    private GUI gui;

    private Family controllables;
    private Family characters;
    private ImmutableArray<Entity> controllablesEntities;
    private ImmutableArray<Entity> charactersEntities;

    private int margin, spacing;

    public UIRenderingSystem(Batch batch) {
        this(batch, 0);
    }

    public UIRenderingSystem(Batch batch, int priority) {
        super(priority);

        controllables = Family.all(Position.class, Controller.class).exclude(Hidden.class).get();
        characters = Family.all(Party.class, Name.class, HP.class, MP.class).get();

        this.batch = batch;

        gui = new GUI(batch);

        gui.addFont("small", GameState.getFontManager().getFont(16));
        gui.addFont("medium", GameState.getFontManager().getFont(24));
        gui.addFont("large", GameState.getFontManager().getFont(30));

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
        GameState.getOverlayCamera().update();
        batch.setProjectionMatrix(GameState.getOverlayCamera().combined);

        batch.begin();

        gui.init();

        gui.createSection("Screen", GUILayout.FlowDirection.NONE);

        gui.setMargin(margin);
        gui.setSpacing(spacing);

        drawStatusBar();

        if (GameState.getState() == RunningState.CRAWL) {
            drawCharactersStats();
        }

        gui.endSection();

        batch.end();

        //gui.showCenters();
    }

    private void drawStatusBar() {
        gui.setFont("small");
        gui.setColor(Color.GREEN);

        gui.createSection("PushToBottom", GUILayout.FlowDirection.VERTICAL);

        String msg = "FPS: " + Gdx.graphics.getFramesPerSecond() + " / Entities: " + GameState.getEngine().getEntities().size();

        gui.pushToEnd(gui.getLabelHeight(msg));

        gui.createSection("StatusBar", GUILayout.FlowDirection.HORIZONTAL);

        gui.Label(msg);

        // display player position
        for (Entity entity : controllablesEntities) {
            Controller controller = cm.get(entity);
            Position pos = pm.get(entity);

            if (controller != null && controller.getState() == GameState.getState()) {
                int x = pos.getX();
                int y = pos.getY();

                msg = "Position: " + x + "," + y;

                gui.pushToEnd(gui.getLabelWidth(msg));

                gui.Label(msg);

                break;
            }
        }

        gui.endSection();

        gui.endSection();
    }

    private void drawCharactersStats() {
        int nPlayers = charactersEntities.size();
        int boxW = 250;
        int boxH = 150;

        float size = nPlayers * boxW + (nPlayers - 1) * spacing + 2 * margin;
        float space = (GameState.getOverlayViewport().getWorldWidth() - size) / 2;

        gui.createSection("Portraits", GUILayout.FlowDirection.HORIZONTAL);

        gui.Spacer(space);

        for (int i = 0; i < nPlayers; i++) {
            for (Entity e : charactersEntities) {
                if (am.get(e).getPos() == i + 1) {
                    gui.createSection("Portrait", GUILayout.FlowDirection.NONE);
                    gui.Box(boxW, boxH);

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

    @Override
    public void dispose() {
    }
}
