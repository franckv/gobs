package com.gobs.components;

import com.badlogic.ashley.core.Component;

public class SpriteRef implements Component {
    String path;
    
    public SpriteRef(String path) {
        this.path = path;
    }
    
    public String getPath() {
        return path;
    }
}
