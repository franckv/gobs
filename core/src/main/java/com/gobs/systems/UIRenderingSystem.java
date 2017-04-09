package com.gobs.systems;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import com.gobs.ui.GUI;
import com.gobs.ui.GdxGUI;
import java.util.Arrays;

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

    private EntitySubscription controllables;
    private EntitySubscription characters;
    private EntitySubscription allEntities;

    private OrthographicDisplay display;
    private FontFactory fontManager;
    private GUI<Color, BitmapFont> gui;

    private int margin, spacing;

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

        gui = new GdxGUI(display, tileManager, batch);

        gui.addFont("small", fontManager.getFont(16));
        gui.addFont("medium", fontManager.getFont(24));
        gui.addFont("large", fontManager.getFont(30));

        gui.load("ui.json");
    }

    @Override
    protected void processSystem() {
        // overlay text is displayed using absolute screen coordinates to avoid scaling font
        display.getCamera().update();
        batch.setProjectionMatrix(display.getCamera().combined);

        batch.begin();

        gui.begin();

        updateStatus();

        if (stateManager.getState() == State.CRAWL) {
            updateCharactersStats();
            gui.enableFragment("characters", true);
        } else {
            gui.enableFragment("characters", false);
        }

        MainLoopStrategy strategy = (MainLoopStrategy) world.getInvocationStrategy();

        if (strategy.hasPerfData()) {
            ObjectMap<String, Long> perfData = strategy.getPerfData();
            StringBuilder perfLabel = new StringBuilder();

            Array<String> systems = perfData.keys().toArray();
            Sort.instance().sort(systems);

            for (String system : systems) {
                perfLabel.append(system).append(": ").append(perfData.get(system)).append("\n");
            }

            gui.enableFragment("perfmon", true);
            gui.setStringValue("perftable", "label", perfLabel.toString());
        }

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

        float size = nPlayers * boxW + (nPlayers - 1) * spacing + 2 * margin;
        float space = (display.getViewPort().getWorldWidth() - size) / 2;

        gui.setIntValue("charactersSpacing", "value", (int) space);

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

    @Override
    public void dispose() {
        fontManager.dispose();
    }
}
