package com.gobs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.gobs.GobsEngine;
import com.gobs.components.Designation;
import com.gobs.components.Pending;
import com.gobs.components.Progress;
import com.gobs.components.WorkItem;
import com.gobs.map.LevelCell;
import com.gobs.map.LevelCell.LevelCellType;
import com.gobs.map.WorldMap;

public class WorkSystem extends EntitySystem {
    ImmutableArray<Entity> entities;
    Family family;
    private WorldMap worldMap;

    private static ComponentMapper<WorkItem> wm = ComponentMapper.getFor(WorkItem.class);
    private static ComponentMapper<Designation> dm = ComponentMapper.getFor(Designation.class);
    private static ComponentMapper<Progress> sm = ComponentMapper.getFor(Progress.class);

    public WorkSystem(WorldMap worldMap) {
        this(worldMap, 0);
    }

    public WorkSystem(WorldMap worldMap, int priority) {
        super(0);

        family = Family.all(WorkItem.class, Designation.class, Progress.class).exclude(Pending.class).get();
        this.worldMap = worldMap;
    }

    @Override
    public boolean checkProcessing() {
        return !((GobsEngine) getEngine()).isRendering() && super.checkProcessing();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        for (Entity entity : entities) {
            Progress progress = sm.get(entity);

            progress.advance();

            if (progress.isComplete()) {
                performTask(entity);
                getEngine().removeEntity(entity);
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

    private void performTask(Entity task) {
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

    private void digMap(Entity entity) {
        Designation design = dm.get(entity);
        System.out.println("Dig at " + design.getX() + "," + design.getY());

        fillArea(design, LevelCell.LevelCellType.FLOOR);
    }

    private void fillMap(Entity entity) {
        Designation design = dm.get(entity);
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
