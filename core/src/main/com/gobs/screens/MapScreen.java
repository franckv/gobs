package com.gobs.screens;

import com.gobs.GameState;
import com.gobs.systems.FPVRenderingSystem;
import com.gobs.systems.MapRenderingSystem;

/**
 *
 */
public class MapScreen extends BaseScreen {
    @Override
    public void show() {
        GameState.getEngine().getSystem(MapRenderingSystem.class).setProcessing(true);
        GameState.getEngine().getSystem(FPVRenderingSystem.class).setProcessing(false);
    }
}
