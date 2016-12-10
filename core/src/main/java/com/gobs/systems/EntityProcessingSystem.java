package com.gobs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Disposable;

public abstract class EntityProcessingSystem extends EntitySystem implements Disposable {
    private Family family;
    private ImmutableArray<Entity> entities;

    public EntityProcessingSystem(Family family) {
        this(family, 0);
    }

    public EntityProcessingSystem(Family family, int priority) {
        super(priority);
        this.family = family;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        entities = engine.getEntitiesFor(family);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        entities = null;
    }

    protected ImmutableArray<Entity> getEntities() {
        return entities;
    }
}
