package com.gobs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gobs.GameState;

/**
 * Assign a display sprite to an entity
 */
public class Sprite implements Component {
    TextureRegion img;

    public Sprite(TextureRegion region) {
        img = region;
    }

    public TextureRegion getTexture() {
        return img;
    }

    public void setTexture(TextureRegion img) {
        this.img = img;
    }
}
