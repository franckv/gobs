package com.gobs.assets;

import com.artemis.ComponentMapper;
import com.artemis.World;
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

public class EntityFactory {
    private ComponentMapper<Position> pm;
    private ComponentMapper<SpriteRef> rm;
    private ComponentMapper<Collider> dm;
    private ComponentMapper<Hidden> nm;
    private ComponentMapper<Party> tm;
    private ComponentMapper<HP> hm;
    private ComponentMapper<MP> mm;
    private ComponentMapper<Name> am;
    private ComponentMapper<Controller> lm;
    private ComponentMapper<Camera> em;
    private ComponentMapper<AI> im;

    private CollisionManager<Integer> collisionManager;
    private TileFactory tileManager;
 
    public EntityFactory(CollisionManager<Integer> collisionManager, TileFactory tileManager) {
        this.collisionManager = collisionManager;
        this.tileManager = tileManager;
    }

    public void loadEntities(World world, String filename) {
        JsonReader reader = new JsonReader();

        JsonValue json = reader.parse(Gdx.files.internal(filename));

        for (JsonValue entity = json.child; entity != null; entity = entity.next) {
            parseEntity(world, entity);
        }
    }

    private void parseEntity(World world, JsonValue entity) {
        JsonValue disabled = entity.get("disabled");
        if (disabled != null && disabled.asBoolean()) {
            return;
        }

        int entityId = world.create();

        JsonValue components = entity.get("components");

        for (JsonValue component = components.child; component != null; component = component.next) {
            parseComponent(entityId, world, component);
        }
    }

    private void parseComponent(int entityId, World world, JsonValue component) {
        String type = component.getString("type");

        switch (type) {
            case "position":
                int x = component.getInt("x");
                int y = component.getInt("y");
                pm.create(entityId).setPosition(x, y);
                break;
            case "sprite":
                if (component.has("res")) {
                    String res = component.getString("res");
                    rm.create(entityId).setPath(res);
                }
                break;
            case "collider":
                dm.create(entityId);
                break;
            case "hidden":
                nm.create(entityId);
                break;
            case "party":
                int pos = component.getInt("pos");
                tm.create(entityId).setPos(pos);
                break;
            case "hp":
                int hp = component.getInt("hp");
                int maxHP = component.getInt("maxHP");
                HP h = hm.create(entityId);
                h.setHP(hp);
                h.setMaxHP(maxHP);
                break;
            case "mp":
                int mp = component.getInt("mp");
                int maxMP = component.getInt("maxMP");
                MP m = mm.create(entityId);
                m.setMP(mp);
                m.setMaxMP(maxMP);
                break;
            case "name":
                String name = component.getString("name");
                am.create(entityId).setName(name);
                break;
            case "controller":
                boolean active = component.getBoolean("active");
                lm.create(entityId).setActive(active);
                break;
            case "camera":
                em.create(entityId).setOrientation(Camera.Orientation.UP);
                break;
            case "ai":
                AIBehavior behavior = new MobBehavior(collisionManager, entityId);
                world.inject(behavior);
                im.create(entityId).setBehavior(behavior);
                break;
        }
    }
}
