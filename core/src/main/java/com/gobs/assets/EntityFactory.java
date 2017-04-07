package com.gobs.assets;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
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
import com.gobs.components.SpriteRef;
import com.gobs.util.CollisionManager;
import java.util.ArrayList;
import java.util.List;

public class EntityFactory {
    private CollisionManager<Entity> collisionManager;
    private TileFactory tileManager;

    public EntityFactory(CollisionManager<Entity> collisionManager, TileFactory tileManager) {
        this.collisionManager = collisionManager;
        this.tileManager = tileManager;
    }

    public List<Entity> loadEntities(String filename) {
        List<Entity> entities = new ArrayList<>();

        JsonReader reader = new JsonReader();

        JsonValue json = reader.parse(Gdx.files.internal(filename));

        for (JsonValue entity = json.child; entity != null; entity = entity.next) {
            Entity e = parseEntity(entity);
            if (e != null) {
                entities.add(e);
            }
        }

        return entities;
    }

    private Entity parseEntity(JsonValue entity) {
        JsonValue disabled = entity.get("disabled");
        if (disabled != null && disabled.asBoolean()) {
            return null;
        }

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

    private Component parseComponent(Entity e, JsonValue component) {
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
                    c = new SpriteRef(res);
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
                boolean active = component.getBoolean("active");
                c = new Controller(active);
                break;
            case "camera":
                c = new Camera(Camera.Orientation.UP);
                break;
            case "ai":
                AIBehavior behavior = new MobBehavior(collisionManager, e);
                c = new AI(behavior);
                break;
        }

        return c;
    }
}
