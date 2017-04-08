package com.gobs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.gobs.components.Animation;
import com.gobs.components.Progress;
import com.gobs.components.WorkItem;

public class ProgressSystem extends LogicSystem {
    ImmutableArray<Entity> entities;
    Family family;

    private static ComponentMapper<Animation> am = ComponentMapper.getFor(Animation.class);
    private static ComponentMapper<WorkItem> wm = ComponentMapper.getFor(WorkItem.class);
    private static ComponentMapper<Progress> sm = ComponentMapper.getFor(Progress.class);

    public ProgressSystem() {
        this(0);
    }

    public ProgressSystem(int priority) {
        super(priority);

        family = Family.one(Animation.class, WorkItem.class).get();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        for (Entity entity : entities) {
            Progress progress = sm.get(entity);
            Animation anim = am.get(entity);
            WorkItem work = wm.get(entity);

            if (progress == null) {
                int duration = 0;
                if (anim != null) {
                    duration = anim.getDuration();
                } else {
                    duration = work.getDuration();
                }

                progress = new Progress(duration);
                entity.add(progress);
            }
        }
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        entities = getEngine().getEntitiesFor(family);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);

        entities = null;
    }
}
