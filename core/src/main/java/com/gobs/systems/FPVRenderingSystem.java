package com.gobs.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.gobs.GobsEngine;
import com.gobs.display.PerspectiveDisplay;
import com.gobs.map.LayerCell;
import com.gobs.map.WorldMap;
import java.util.ArrayList;
import java.util.List;

public class FPVRenderingSystem extends EntitySystem implements Disposable {
    private final static float h = 0.1f;

    private WorldMap worldMap;
    private PerspectiveDisplay display;
    private Environment environment;
    private ModelBatch modelBatch;
    private Model wall, floor;
    private List<ModelInstance> instances = new ArrayList<>();
    private PointLight light;
    private Texture texture;
    private float step;

    public FPVRenderingSystem(PerspectiveDisplay display, WorldMap worldMap) {
        this(display, worldMap, 0);
    }

    public FPVRenderingSystem(PerspectiveDisplay display, WorldMap worldMap, int priority) {
        super(priority);

        this.worldMap = worldMap;
        this.display = display;

        texture = new Texture("textures/wall.png");

        modelBatch = new ModelBatch();

        light = new PointLight().set(0.8f, 0.8f, 0.8f, 0f, 0f, 0f, 1f);
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(light);

        int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
        Material mat = new Material(TextureAttribute.createDiffuse(texture));

        step = display.getStepSize();

        ModelBuilder modelBuilder = new ModelBuilder();
        wall = modelBuilder.createBox(step, step, step, mat, attr);

        floor = modelBuilder.createBox(worldMap.getWorldWidth() * step, worldMap.getWorldHeight() * step, h,
                new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    }

    private void buildScene() {
        ModelInstance instance;

        instances.clear();

        instance = new ModelInstance(floor);
        instance.transform.translate(worldMap.getWorldWidth() * step / 2 - step / 2, worldMap.getWorldHeight() * step / 2 - step / 2, -(step + h) / 2);
        instances.add(instance);
        for (LayerCell c : worldMap.getCurrentLayer()) {
            if (c != null) {
                switch (c.getType()) {
                    case WALL:
                        instance = new ModelInstance(wall);
                        instance.transform.translate(c.getX() * step, c.getY() * step, 0);
                        instance.transform.rotate(new Vector3(0, -1, 0), 90);

                        instances.add(instance);
                        break;
                }
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);

        super.update(deltaTime);

        display.getCamera().update();

        if (worldMap.getCurrentLayer().isDirty()) {
            buildScene();
        }

        light.setPosition(display.getCamera().position);

        modelBatch.begin(display.getCamera());

        modelBatch.render(instances, environment);

        modelBatch.end();
    }

    @Override
    public boolean checkProcessing() {
        return ((GobsEngine) getEngine()).isRendering() && super.checkProcessing();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        wall.dispose();
        floor.dispose();
        texture.dispose();
    }
}
