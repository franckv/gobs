package com.gobs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.gobs.assets.TileFactory;
import com.gobs.components.Sprite;
import com.gobs.components.SpriteRef;

public class AssetSystem extends IteratingSystem {
    @Wire
    private TileFactory tileManager;

    private ComponentMapper<SpriteRef> rm;
    private ComponentMapper<Sprite> sm;

    public AssetSystem() {
        super(Aspect.all(SpriteRef.class).exclude(Sprite.class));
    }

    @Override
    public boolean checkProcessing() {
        return super.checkProcessing();
        //return !((GobsEngine) getEngine()).isRendering() && super.checkProcessing();
    }

    @Override
    protected void process(int entityId) {
        SpriteRef spriteRef = rm.get(entityId);

        Sprite sprite = sm.create(entityId);
        sprite.setTexture(tileManager.resolveTexture(spriteRef.getPath()));
    }
}
