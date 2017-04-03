package com.gobs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
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
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.gobs.components.Camera;
import com.gobs.components.Position;
import com.gobs.display.PerspectiveDisplay;
import com.gobs.map.Layer;
import com.gobs.map.LayerCell;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FPVRenderingSystem extends EntityProcessingSystem {
    private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
    private ComponentMapper<Camera> cm = ComponentMapper.getFor(Camera.class);

    private Layer mapLayer;
    private PerspectiveDisplay display;
    private int worldWidth;
    private int worldHeight;
    private Environment environment;
    private ModelBatch modelBatch;
    private Model wall, floor;
    private List<ModelInstance> instances = new ArrayList<>();
    private float step = 1f;
    private float h = 0.1f;
    private DirectionalLight light;
    private Texture texture;

    public FPVRenderingSystem(PerspectiveDisplay displayManager, int worldWidth, int worldHeight, Layer mapLayer) {
        this(displayManager, worldWidth, worldHeight, mapLayer, 0);
    }

    public FPVRenderingSystem(PerspectiveDisplay displayManager, int worldWidth, int worldHeight, Layer mapLayer, int priority) {
        super(Family.all(Camera.class).get(), priority);

        this.mapLayer = mapLayer;
        this.display = displayManager;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;

        texture = new Texture("textures/wall.png");

        modelBatch = new ModelBatch();

        light = new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0f, 1f, 0f);
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(light);

        int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
        Material mat = new Material(TextureAttribute.createDiffuse(texture));

        ModelBuilder modelBuilder = new ModelBuilder();
        wall = modelBuilder.createBox(step, step, step, mat, attr);

        floor = modelBuilder.createBox(worldWidth * step, worldHeight * step, h,
                new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    }

    private void buildScene() {
        ModelInstance instance;

        instances.clear();

        instance = new ModelInstance(floor);
        instance.transform.translate(worldWidth * step / 2 - step / 2, worldHeight * step / 2 - step / 2, -(step + h) / 2);
        instances.add(instance);

        for (LayerCell c : mapLayer) {
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

        for (Entity entity : getEntities()) {
            Camera cam = cm.get(entity);
            Position pos = pm.get(entity);

            display.getCamera().position.set(pos.getX() * step, pos.getY() * step, 0f);

            int dx = 0, dy = 0;

            switch (cam.getOrientation()) {
                case UP:
                    dy = 1;
                    break;
                case DOWN:
                    dy = -1;
                    break;
                case LEFT:
                    dx = -1;
                    break;
                case RIGHT:
                    dx = 1;
                    break;

            }

            Vector3 vec = new Vector3((pos.getX() + dx) * step, (pos.getY() + dy) * step, 0f);
            display.getCamera().lookAt(vec);
        }

        display.getCamera().update();

        if (mapLayer.isDirty()) {
            buildScene();
            mapLayer.setDirty(false);
        }

        modelBatch.begin(display.getCamera());

        modelBatch.render(instances, environment);

        modelBatch.end();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        wall.dispose();
        floor.dispose();
        texture.dispose();
    }
}
