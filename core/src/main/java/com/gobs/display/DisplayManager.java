package com.gobs.display;

public class DisplayManager {
    private int tileSize;

    MapDisplay mapDisplay;
    OrthographicDisplay overlayDisplay;
    PerspectiveDisplay fpvDisplay;

    public DisplayManager(int screenWidth, int screenHeight, int worldWidth, int worldHeight, int tileSize) {
        this.tileSize = tileSize;

        mapDisplay = new MapDisplay(screenWidth, screenHeight, tileSize);
        overlayDisplay = new OrthographicDisplay(screenWidth, screenHeight);
        fpvDisplay = new PerspectiveDisplay(screenWidth, screenHeight);
    }

    public MapDisplay getMapDisplay() {
        return mapDisplay;
    }

    public OrthographicDisplay getOverlayDisplay() {
        return overlayDisplay;
    }

    public PerspectiveDisplay getFPVDisplay() {
        return fpvDisplay;
    }

    public void resize(int width, int height) {
        getMapDisplay().update(width, height);
        getOverlayDisplay().update(width, height);
        getFPVDisplay().update(width, height);
    }

    public int getTileSize() {
        return tileSize;
    }
}
