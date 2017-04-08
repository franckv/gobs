package com.gobs.systems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.gobs.components.Animation;
import com.gobs.components.Progress;
import com.gobs.components.WorkItem;

public class ProgressSystem extends BaseEntitySystem {
    private static ComponentMapper<Animation> am;
    private static ComponentMapper<WorkItem> wm;
    private static ComponentMapper<Progress> sm;

    public ProgressSystem() {
        super(Aspect.one(Animation.class, WorkItem.class));
    }

    @Override
    protected void processSystem() {
        for (int i = 0; i < getEntityIds().size(); i++) {
            int entityId = getEntityIds().get(i);

            Progress progress = sm.get(entityId);
            Animation anim = am.get(entityId);
            WorkItem work = wm.get(entityId);

            if (progress == null) {
                int duration = 0;
                if (anim != null) {
                    duration = anim.getDuration();
                } else {
                    duration = work.getDuration();
                }

                progress = sm.create(entityId);
                progress.setDuration(duration);
            }
        }
    }
}
