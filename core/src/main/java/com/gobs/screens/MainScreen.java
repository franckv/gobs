package com.gobs.screens;

import com.gobs.GameState;
import com.gobs.systems.FPVRenderingSystem;
import com.gobs.systems.MapRenderingSystem;

public class MainScreen extends BaseScreen {
    public MainScreen() {
    }

    @Override
    public void show() {
        GameState.getEngine().getSystem(MapRenderingSystem.class).setProcessing(false);
        GameState.getEngine().getSystem(FPVRenderingSystem.class).setProcessing(true);
    }
}
