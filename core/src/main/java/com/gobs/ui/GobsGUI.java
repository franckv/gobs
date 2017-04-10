package com.gobs.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gobs.assets.TileFactory;
import com.gobs.display.OrthographicDisplay;
import com.gobs.input.InputMap;
import com.gobs.ui.gdx.GdxGUI;

public class GobsGUI extends GdxGUI {
    private InputMap inputMap;
    private OrthographicDisplay display;
    private TileFactory tileManager;

    private TextureRegion frame;
    private TextureRegion frameSelected;

    public GobsGUI(OrthographicDisplay display, TileFactory tileManager, Batch batch) {
        super(batch);

        this.display = display;
        this.tileManager = tileManager;

        frame = tileManager.getFrame();
        frameSelected = tileManager.getFrameSelected();
    }

    public boolean acceptInput(InputMap inputMap) {
        this.inputMap = inputMap;

        return false;
    }

    @Override
    protected boolean isMouseDown() {
        return (inputMap != null && inputMap.isMouseDown());
    }

    @Override
    protected int getMouseX() {
        int x = -1;

        if (inputMap != null) {
            x = inputMap.getMouseX();
        }

        return x;
    }

    @Override
    protected int getMouseY() {
        int y = -1;

        if (inputMap != null) {
            y = inputMap.getMouseY();
        }

        return y;
    }

    @Override
    public float getMaxWidth() {
        return display.getViewPort().getWorldWidth();
    }

    @Override
    public float getMaxHeight() {
        return display.getViewPort().getWorldHeight();
    }

    @Override
    protected OrthographicCamera getCamera() {
        return display.getCamera();
    }

    @Override
    protected TextureRegion getFrame(boolean selected) {
        if (selected) {
            return frameSelected;
        } else {
            return frame;
        }
    }
}
