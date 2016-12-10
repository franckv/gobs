package com.gobs.assets;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.gobs.GameState;
import com.gobs.RunningState;
import com.gobs.ai.AIBehavior;
import com.gobs.ai.MobBehavior;
import com.gobs.components.AI;
import com.gobs.components.Camera;
import com.gobs.components.Collider;
import com.gobs.components.Controller;
import com.gobs.components.HP;
import com.gobs.components.Hidden;
import com.gobs.components.MP;
import com.gobs.components.Name;
import com.gobs.components.Party;
import com.gobs.components.Position;
import com.gobs.components.Sprite;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntityFactory {
    private static Pattern resourcePattern = Pattern.compile("(^.*)!(\\d+),(\\d+):(\\d+),(\\d+)$");

    public static List<Entity> loadEntities(String filename) {
        List<Entity> entities = new ArrayList<>();

        JsonReader reader = new JsonReader();

        JsonValue json = reader.parse(Gdx.files.internal(filename));

        for (JsonValue entity = json.child; entity != null; entity = entity.next) {
            Entity e = parseEntity(entity);
            entities.add(e);
        }

        return entities;
    }

    private static Entity parseEntity(JsonValue entity) {
        Entity e = new Entity();

        JsonValue components = entity.get("components");

        for (JsonValue component = components.child; component != null; component = component.next) {
            Component c = parseComponent(e, component);
            if (c != null) {
                e.add(c);
            }
        }

        return e;
    }

    private static Component parseComponent(Entity e, JsonValue component) {
        Component c = null;

        String type = component.getString("type");

        switch (type) {
            case "position":
                int x = component.getInt("x");
                int y = component.getInt("y");
                c = new Position(x, y);
                break;
            case "sprite":
                if (component.has("res")) {
                    String res = component.getString("res");
                    c = new Sprite(getTexture(res));
                } else {
                    String colorValue = component.getString("color");

                    Color color = Color.BLACK;
                    try {
                        color = (Color) Color.class.getDeclaredField(colorValue).get(null);
                    } catch (NoSuchFieldException | SecurityException
                            | IllegalArgumentException | IllegalAccessException ex) {
                        Gdx.app.error("JSON", "Invalid color: " + colorValue);
                    }

                    if (!component.has("fill") || component.getBoolean("fill")) {
                        c = new Sprite(GameState.getTileManager().getFullTile(color));
                    } else {
                        c = new Sprite(GameState.getTileManager().getRectTile(color));
                    }
                }
                break;
            case "collider":
                c = new Collider();
                break;
            case "hidden":
                c = new Hidden();
                break;
            case "party":
                int pos = component.getInt("pos");
                c = new Party(pos);
                break;
            case "hp":
                int hp = component.getInt("hp");
                int maxHP = component.getInt("maxHP");
                c = new HP(hp, maxHP);
                break;
            case "mp":
                int mp = component.getInt("mp");
                int maxMP = component.getInt("maxMP");
                c = new MP(mp, maxMP);
                break;
            case "name":
                String name = component.getString("name");
                c = new Name(name);
                break;
            case "controller":
                RunningState state;
                String stateValue = component.getString("state");
                if (stateValue.equals("MOVE")) {
                    state = RunningState.CRAWL;
                } else {
                    state = RunningState.SELECT;
                }
                c = new Controller(state);
                break;
            case "camera":
                stateValue = component.getString("state");
                if (stateValue.equals("MOVE")) {
                    state = RunningState.CRAWL;
                } else {
                    state = RunningState.SELECT;
                }
                c = new Camera(state, Camera.Orientation.UP);
                break;
            case "ai":
                AIBehavior behavior = new MobBehavior(e);
                c = new AI(behavior);
                break;
        }

        return c;
    }

    private static TextureRegion getTexture(String res) {
        AssetManager manager = GameState.getAssetManager();

        String textureName = res;

        Matcher matcher = resourcePattern.matcher(res);
        if (matcher.matches()) {
            textureName = matcher.group(1);
        }

        if (matcher.matches()) {
            int x = Integer.parseInt(matcher.group(2));
            int y = Integer.parseInt(matcher.group(3));
            int w = Integer.parseInt(matcher.group(4));
            int h = Integer.parseInt(matcher.group(5));

            return GameState.getTileManager().getTile(textureName, x, y, w, h);
        } else {
            return GameState.getTileManager().getTile(textureName);
        }
    }
}
