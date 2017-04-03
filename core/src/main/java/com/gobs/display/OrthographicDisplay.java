package com.gobs.display;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class OrthographicDisplay extends Display {
    private OrthographicCamera camera;

    public OrthographicDisplay(int width, int height) {
        super(width, height);
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
        
        viewPort = new FitViewport(width, height, camera);
        viewPort.apply();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
