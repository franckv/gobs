package com.gobs.systems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.gobs.components.Designation;
import com.gobs.components.Pending;
import com.gobs.components.Progress;
import com.gobs.components.WorkItem;
import com.gobs.map.LevelCell;
import com.gobs.map.LevelCell.LevelCellType;
import com.gobs.map.WorldMap;

public class WorkSystem extends BaseEntitySystem {
    private static ComponentMapper<WorkItem> wm;
    private static ComponentMapper<Designation> dm;
    private static ComponentMapper<Progress> sm;

    @Wire
    private WorldMap worldMap;

    public WorkSystem() {
        super(Aspect.all(WorkItem.class, Designation.class, Progress.class).exclude(Pending.class));
    }

    @Override
    protected void processSystem() {
        for (int i = 0; i < getEntityIds().size(); i++) {
            int entityId = getEntityIds().get(i);

            Progress progress = sm.get(entityId);

            progress.advance();

            if (progress.isComplete()) {
                performTask(entityId);
                getWorld().delete(entityId);
            }
        }
    }

    private void performTask(int task) {
        WorkItem work = wm.get(task);

        switch (work.getType()) {
            case DIGGING:
                digMap(task);
                break;
            case FILLING:
                fillMap(task);
                break;
        }
    }

    private void digMap(int entityId) {
        Designation design = dm.get(entityId);
        System.out.println("Dig at " + design.getX() + "," + design.getY());

        fillArea(design, LevelCell.LevelCellType.FLOOR);
    }

    private void fillMap(int entityId) {
        Designation design = dm.get(entityId);
        System.out.println("Fill " + design.getX() + "," + design.getY());

        fillArea(design, LevelCell.LevelCellType.WALL);
    }

    private void fillArea(Designation design, LevelCellType cellType) {
        for (int i = 0; i < design.getWidth(); i++) {
            for (int j = 0; j < design.getHeight(); j++) {
                worldMap.getCurrentLevel().setCell(design.getX() + i, design.getY() + j, cellType);
            }
        }
    }
}
