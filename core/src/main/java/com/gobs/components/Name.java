package com.gobs.components;

import com.badlogic.ashley.core.Component;

/**
 * Character name
 */
public class Name implements Component {
    String name;
    
    public Name(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}
