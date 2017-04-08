package com.gobs.systems;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.gobs.display.PerspectiveDisplay;
import com.gobs.map.LevelCell;
import com.gobs.map.WorldMap;

public class FPVRenderingSystem extends BaseSystem implements Disposable {
    private final static float h = 0.1f;

    @Wire
    private WorldMap worldMap;

    private PerspectiveDisplay display;
    private Environment environment;
    private ModelBatch modelBatch;
    private Model wall, floor;
    private Array<ModelInstance> instances = new Array<>();
    private PointLight light;
    private Texture texture;
    private float step;
    private boolean init = true;

    public FPVRenderingSystem(PerspectiveDisplay display) {
        this.display = display;
    }

    @Override
    protected void initialize() {
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
        for (LevelCell c : worldMap.getCurrentLevel()) {
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
    protected void processSystem() {
        clearScreen();

        display.getCamera().update();

        if (init || worldMap.getCurrentLevel().isDirty()) {
            buildScene();
            init = false;
        }

        light.setPosition(display.getCamera().position);

        modelBatch.begin(display.getCamera());

        modelBatch.render(instances, environment);

        modelBatch.end();
    }

    @Override
    public boolean checkProcessing() {
        return super.checkProcessing();
        //return ((GobsEngine) getEngine()).isRendering() && super.checkProcessing();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        wall.dispose();
        floor.dispose();
        texture.dispose();
    }
}
