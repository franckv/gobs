package com.gobs.display;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class PerspectiveDisplay extends Display {
    private PerspectiveCamera camera;
    
    public PerspectiveDisplay(int width, int height) {
        super(width, height);

        camera = new PerspectiveCamera(67, width, height);
        camera.near = 0.1f;
        camera.far = 200f;
        camera.up.set(0, 0, 1);

        viewPort = new FitViewport(width, height, camera);
        viewPort.apply();
    }
    
    public PerspectiveCamera getCamera() {
        return camera;
    }
}
