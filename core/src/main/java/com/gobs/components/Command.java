package com.gobs.components;

import com.artemis.Component;

/**
 * Make an entity execute an input command
 */
public class Command extends Component {
    public enum CommandType {
        UP, DOWN, LEFT, RIGHT
    }

    CommandType type;

    public CommandType getCommand() {
        return type;
    }

    public void setCommand(CommandType type) {
        this.type = type;
    }
}
