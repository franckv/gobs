package com.gobs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.gobs.GobsEngine;
import com.gobs.assets.TileFactory;
import com.gobs.components.Sprite;
import com.gobs.components.SpriteRef;

public class AssetSystem extends IteratingSystem {
    private TileFactory tileManager;

    private ComponentMapper<SpriteRef> sm = ComponentMapper.getFor(SpriteRef.class);

    public AssetSystem(TileFactory tileManager) {
        this(tileManager, 0);
    }

    public AssetSystem(TileFactory tileManager, int priority) {
        super(Family.all(SpriteRef.class).exclude(Sprite.class).get(), priority);

        this.tileManager = tileManager;
    }

    @Override
    public boolean checkProcessing() {
        return !((GobsEngine) getEngine()).isRendering() && super.checkProcessing();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SpriteRef spriteRef = sm.get(entity);
        
        Sprite sprite = new Sprite(tileManager.resolveTexture(spriteRef.getPath()));
        
        entity.add(sprite);
    }
}
