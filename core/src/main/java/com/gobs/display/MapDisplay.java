package com.gobs.display;

public class MapDisplay extends OrthographicDisplay {
    private int tileSize;

    public MapDisplay(int width, int height, int tileSize) {
        super(width / tileSize, height / tileSize);
        this.tileSize = tileSize;
    }

    @Override
    public void update(int width, int height) {
        super.update(width / tileSize, height / tileSize);
    }
}
