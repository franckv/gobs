package com.gobs.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
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

    private Vector2 getMouseCoordinate() {
        Vector2 screenCoord = new Vector2(inputMap.getMouseX(), inputMap.getMouseY());

        // transform screen coord to world coord
        return display.getViewPort().unproject(screenCoord);
    }

    @Override
    protected boolean isMouseDown() {
        return (inputMap != null && inputMap.isMouseDown());
    }

    @Override
    protected int getMouseX() {
        int x = -1;

        if (inputMap != null) {
            x = (int) getMouseCoordinate().x;
        }

        return x;
    }

    @Override
    protected int getMouseY() {
        int y = -1;

        if (inputMap != null) {
            y = (int) getMouseCoordinate().y;
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
    protected TextureRegion getFrame(boolean selected) {
        if (selected) {
            return frameSelected;
        } else {
            return frame;
        }
    }

    @Override
    protected TextureRegion getSolidTexture(Color color) {
        return tileManager.getFullTile(color);
    }

    @Override
    protected TextureRegion getImage(String resource) {
        switch (resource) {
            case "frame":
                return tileManager.getFrame();
            case "frame_selected":
                return tileManager.getFrameSelected();
        }

        return null;
    }
}
