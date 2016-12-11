package com.gobs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

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
