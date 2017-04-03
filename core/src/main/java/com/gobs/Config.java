package com.gobs;

import com.badlogic.gdx.Gdx;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private Properties props;
    private boolean logging;
    private int tileSize;
    private int worldWidth, worldHeight;
    private int repeat;
    private String frameSprite;
    private String frameSelectedSprite;

    public Config() {
        props = new Properties();
    }

    public Config(String res) {
        props = new Properties();

        try {
            props.load(Gdx.files.internal(res).read());
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize config", e);
        }

        logging = props.getProperty("logging").equals("true");
        tileSize = getInt("tilesize", 16);
        worldWidth = getInt("worldWidth", 100);
        worldHeight = getInt("worldHeight", 100);
        repeat = getInt("repeat", 20);
        frameSprite = getString("frameSprite", "");
        frameSelectedSprite = getString("frameSelectedSprite", "");
    }

    final int getInt(String prop, int val) {
        if (props.containsKey(prop)) {
            return Integer.parseInt(props.getProperty(prop));
        }

        return val;
    }

    final String getString(String prop, String val) {
        if (props.containsKey(prop)) {
            return props.getProperty(prop);
        }

        return val;
    }

    public boolean getLogging() {
        return logging;
    }

    public int getTileSize() {
        return tileSize;
    }

    public int getWorldWidth() {
        return worldWidth;
    }

    public int getWorldHeight() {
        return worldHeight;
    }

    public int getRepeat() {
        return repeat;
    }

    public String getFrameSprite() {
        return frameSprite;
    }

    public String getFrameSelectedSprite() {
        return frameSelectedSprite;
    }
}
