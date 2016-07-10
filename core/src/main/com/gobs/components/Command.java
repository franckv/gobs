package com.gobs.components;

import com.badlogic.ashley.core.Component;

/**
 * Make an entity execute an input command
 */
public class Command implements Component {
    public enum CommandType {
        UP, DOWN, LEFT, RIGHT
    }
    
    CommandType type;
    
    public Command(CommandType type) {
        this.type = type;
    }

    public CommandType getCommand() {
        return type;
    }
}
