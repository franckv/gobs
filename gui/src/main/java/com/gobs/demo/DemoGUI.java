package com.gobs.demo;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gobs.ui.GUILayout;
import com.gobs.ui.gdx.GdxGUI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class DemoGUI extends GdxGUI implements ApplicationListener, InputProcessor {
    Batch batch;
    OrthographicCamera camera;
    Viewport viewport;

    List<GUILayout> layouts;

    boolean showLayouts = false;

    int idx = 0;

    @Override
    public void begin() {
        batch.begin();
        super.begin();

        if (showLayouts) {
            layouts = new ArrayList<>();
            idx = 0;
        }
    }

    @Override
    public void end() {
        super.end();
        batch.end();

        if (showLayouts) {
            drawLayouts();
        }
    }

    private void drawLayouts() {
        for (int i = layouts.size() - 1; i >= 0; i--) {
            GUILayout layout = layouts.get(i);

            Color[] colors = {
                Color.GREEN, Color.RED, Color.BLUE, Color.YELLOW,
                Color.PINK, Color.ORANGE, Color.MAGENTA, Color.PURPLE
            };

            idx = (idx + 1) % colors.length;

            drawSquare(layout.getLeft(), layout.getBottom(), layout.getRight(), layout.getTop(), colors[idx]);
        }

        layouts = null;
    }

    protected void setShowLayouts(boolean showLayouts) {
        this.showLayouts = showLayouts;
    }

    public void showCenters(int unit) {
        Gdx.gl20.glLineWidth(1);
        getRenderer().setProjectionMatrix(getCamera().combined);
        getRenderer().begin(ShapeRenderer.ShapeType.Line);
        getRenderer().setColor(Color.RED);

        int size = unit / 2;

        float h = Gdx.graphics.getHeight();
        float w = Gdx.graphics.getWidth();

        // vertical
        getRenderer().line(w / 2.0f, 0, w / 2.0f, h);

        for (int i = 0; i < h / unit; i++) {
            float y = i * unit;
            getRenderer().line(w / 2.0f - size, y, w / 2.0f + size, y);
        }

        // horizontal
        getRenderer().line(0, h / 2.0f, w, h / 2.0f);

        for (int i = 0; i < w / unit; i++) {
            float x = i * unit;
            getRenderer().line(x, h / 2.0f - size, x, h / 2.0f + size);
        }

        getRenderer().end();
    }

    public void showRuler(int unit) {
        Gdx.gl20.glLineWidth(1);
        getRenderer().setProjectionMatrix(getCamera().combined);
        getRenderer().begin(ShapeRenderer.ShapeType.Line);
        getRenderer().setColor(Color.RED);

        int size = unit;

        float h = Gdx.graphics.getHeight();
        float w = Gdx.graphics.getWidth();

        for (int i = 0; i < h / unit; i++) {
            float y = h - i * unit;
            getRenderer().line(0, y, size, y);
        }

        for (int i = 0; i < w / unit; i++) {
            float x = i * unit;
            getRenderer().line(x, h, x, h - size);
        }

        getRenderer().end();
    }

    @Override
    protected void disposeLayout(GUILayout layout) {
        if (showLayouts) {
            layouts.add(layout);
        }
    }

    @Override
    protected TextureRegion getFrame(boolean selected
    ) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected OrthographicCamera getCamera() {
        return camera;
    }

    @Override
    public float getMaxWidth() {
        return viewport.getWorldWidth();
    }

    @Override
    public float getMaxHeight() {
        return viewport.getWorldHeight();
    }

    @Override
    protected boolean isMouseDown() {
        return false;
    }

    @Override
    protected int getMouseX() {
        return 0;
    }

    @Override
    protected int getMouseY() {
        return 0;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();

        setBatch(batch);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        viewport.apply();

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void resize(int width, int height
    ) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        super.dispose();

        batch.dispose();
    }

    protected BitmapFont getFont(String file, int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(file));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        BitmapFont font = generator.generateFont(parameter);

        return font;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE) {
            Gdx.app.exit();
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
