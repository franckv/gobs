package com.gobs.components;

import com.artemis.Component;
import com.artemis.annotations.Transient;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Assign a display sprite to an entity
 */
@Transient
public class Sprite extends Component {
    TextureRegion img;

    public TextureRegion getTexture() {
        return img;
    }

    public void setTexture(TextureRegion img) {
        this.img = img;
    }
}
