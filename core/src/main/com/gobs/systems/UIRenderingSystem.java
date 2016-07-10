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
import com.gobs.ui.GUI.FontSize;

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

    public UIRenderingSystem(Batch batch) {
        this(batch, 0);
    }

    public UIRenderingSystem(Batch batch, int priority) {
        super(priority);

        controllables = Family.all(Position.class, Controller.class).exclude(Hidden.class).get();
        characters = Family.all(Party.class, Name.class, HP.class, MP.class).get();

        this.batch = batch;

        gui = new GUI(batch);
        gui.setSpacing(5);

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

        drawStatusBar();
        if (GameState.getState() == RunningState.CRAWL) {
            drawCharactersStats();
        }

        batch.end();

        //gui.showCenters();
    }

    private void drawStatusBar() {
        gui.setMargin(5);
        gui.setSpacing(5);
        gui.setFontSize(FontSize.SMALL);
        gui.setColor(Color.GREEN);
        gui.setFlow(GUI.FlowDirection.RIGHT);

        String msg = "FPS: " + Gdx.graphics.getFramesPerSecond() + " / Entities: " + GameState.getEngine().getEntities().size();
        gui.setAlign(GUI.AlignH.LEFT, GUI.AlignV.BOTTOM);
        gui.Label(msg);

        // display player position
        for (Entity entity : controllablesEntities) {
            Controller controller = cm.get(entity);
            Position pos = pm.get(entity);

            if (controller != null && controller.getState() == GameState.getState()) {
                int x = pos.getX();
                int y = pos.getY();

                msg = "Position: " + x + "," + y;
                gui.setAlign(GUI.AlignH.RIGHT, GUI.AlignV.BOTTOM);
                gui.Label(msg);
                break;
            }
        }
    }

    private void drawCharactersStats() {
        int nPlayers = charactersEntities.size();

        /*
               Draw character boxes     
         */
        int boxW = 250;
        int boxH = 150;

        // offset to center a group of n widgets is -(n-1)/2 * (size + spacing)
        gui.setAlign(GUI.AlignH.CENTER, GUI.AlignV.TOP, -(nPlayers - 1) * (boxW + 5) / 2, 0);

        for (int i = 0; i < nPlayers; i++) {
            gui.pushState();
            gui.Box(boxW, boxH);
        }

        /*
               Draw character states
         */
        int textMarginX = 30;
        int textMarginY = 20;
        int textSpacing = 7;

        gui.setColor(Color.WHITE);
        gui.setFontSize(FontSize.LARGE);

        for (int i = 0; i < nPlayers; i++) {
            for (Entity e : charactersEntities) {
                if (am.get(e).getPos() == nPlayers - i) {
                    gui.popState();
                    gui.setFlow(GUI.FlowDirection.DOWN);
                    gui.setMargin(textMarginX, textMarginY);
                    gui.setSpacing(3 * textSpacing);
                    gui.setFontSize(FontSize.LARGE);
                    gui.Label(nm.get(e).getName());
                    gui.setFontSize(FontSize.MEDIUM);
                    gui.setSpacing(textSpacing);
                    gui.setAlignH(GUI.AlignH.NONE, -boxW / 2 + textMarginX);
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
                    gui.setColor(Color.WHITE);
                    gui.Label("LV: 99");
                }
            }
        }
    }

    @Override
    public void dispose() {
    }
}
