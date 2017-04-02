/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gobs.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;

import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class DisplayManager {
    // camera to display map items
    private OrthographicCamera mapCamera;
    // camera to display overlay text
    private OrthographicCamera overlayCamera;
    // camera for FPV
    private PerspectiveCamera fpvCamera;

    private Viewport mapViewPort;
    private Viewport overlayViewPort;
    private Viewport fpvViewPort;

    private int screenWidth;
    private int screenHeight;
    private float screenRatio;

    private int worldWidth;
    private int worldHeight;

    private int mapWidth;
    private int mapHeight;

    private int tileSize;
    
    public DisplayManager(int worldWidth, int worldHeight, int tileSize) {
        // screen resolution
        // TODO: update when resizing
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        screenRatio = (float) screenWidth / screenHeight;

        // size of the whole map
        this.worldHeight = worldHeight;
        this.worldWidth = worldWidth;
        this.tileSize = tileSize;
        
        // part of the map visible on screen
        mapWidth = (int) screenWidth / tileSize;
        mapHeight = (int) screenHeight / tileSize;

        mapCamera = new OrthographicCamera();
        mapCamera.setToOrtho(false, mapWidth, mapHeight);

        mapViewPort = new FitViewport(mapWidth, mapHeight, mapCamera);
        mapViewPort.apply();

        overlayCamera = new OrthographicCamera();
        overlayCamera.setToOrtho(false, screenWidth, screenHeight);

        overlayViewPort = new FitViewport(screenWidth, screenHeight, overlayCamera);
        overlayViewPort.apply();

        fpvCamera = new PerspectiveCamera(67, screenWidth, screenHeight);
        fpvCamera.near = 0.1f;
        fpvCamera.far = 200f;
        fpvCamera.up.set(0, 0, 1);

        fpvViewPort = new FitViewport(screenWidth, screenHeight, fpvCamera);
        fpvViewPort.apply();
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getWorldHeight() {
        return worldHeight;
    }

    public int getWorldWidth() {
        return worldWidth;
    }

    public Viewport getMapViewport() {
        return mapViewPort;
    }

    public Viewport getOverlayViewport() {
        return overlayViewPort;
    }

    public float getScreenRatio() {
        return screenRatio;
    }

    public Viewport getFPVViewport() {
        return fpvViewPort;
    }

    public OrthographicCamera getMapCamera() {
        return mapCamera;
    }

    public OrthographicCamera getOverlayCamera() {
        return overlayCamera;
    }

    public PerspectiveCamera getFPVCamera() {
        return fpvCamera;
    }
    
    public int getTileSize() {
        return tileSize;
    }
}