package com.gobs.input;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gobs.input.InputMap.Input;
import java.util.BitSet;

public class ContextManager {
    public enum ContextType {
        GLOBAL, CRAWLING, MAP, EDITMAP, INVENTORY
    }

    public enum Action {
        SHOW_MAP, SHOW_INVENTORY, TOGGLE_EDIT, TOGGLE_VIEW,
        DIG, FILL, TARGET, COMPLETE,
        MOVE_LEFT, MOVE_RIGHT, MOVE_UP, MOVE_DOWN,
        SCROLL_LEFT, SCROLL_RIGHT, SCROLL_UP, SCROLL_DOWN,
        ZOOM_IN, ZOOM_OUT, INVENTORY,
        EXIT, DUMP, DEBUG
    }

    public static class Event {
        ContextType context;
        Action action;

        public Event(ContextType context, Action action) {
            this.context = context;
            this.action = action;
        }

        public ContextType getContext() {
            return context;
        }

        public Action getAction() {
            return action;
        }
    }

    BitSet activeContexts;
    // map input to actions in a specific context
    ObjectMap<ContextType, ObjectMap<Input, Action>> inputMappings;
    // callback registration for actions
    ObjectMap<ContextType, ObjectMap<Action, Array<ContextHandler>>> handlerMappings;
    // poll based consummer registration for actions
    ObjectMap<ContextType, ObjectMap<Action, Array<String>>> consummerMappings;
    // list of events available for a specific consummer
    ObjectMap<String, Array<Event>> dispatcher;

    public ContextManager() {
        activeContexts = new BitSet(ContextType.values().length);
        inputMappings = new ObjectMap<>();
        handlerMappings = new ObjectMap<>();
        consummerMappings = new ObjectMap<>();
        dispatcher = new ObjectMap<>();
    }

    public void activateContext(ContextType context) {
        activeContexts.set(context.ordinal());
    }

    public void disableContext(ContextType context) {
        activeContexts.clear(context.ordinal());
    }

    public void mapInput(ContextType context, Input input, Action action) {
        ObjectMap<Input, Action> inputMapping;
        if (!inputMappings.containsKey(context)) {
            inputMapping = new ObjectMap<>();
            inputMappings.put(context, inputMapping);
        } else {
            inputMapping = inputMappings.get(context);
        }

        inputMapping.put(input, action);
    }

    public void registerConsumer(String consummer, ContextType context, Action action) {
        ObjectMap<Action, Array<String>> consummerMapping;
        if (!consummerMappings.containsKey(context)) {
            consummerMapping = new ObjectMap<>();
            consummerMappings.put(context, consummerMapping);
        } else {
            consummerMapping = consummerMappings.get(context);
        }

        if (!consummerMapping.containsKey(action)) {
            consummerMapping.put(action, new Array<>());
        }
        consummerMapping.get(action).add(consummer);
        if (!dispatcher.containsKey(consummer)) {
            dispatcher.put(consummer, new Array<>());
        }
    }

    public void dispatchInput(InputMap inputmap) {
        for (Input input : inputmap.getPressed()) {
            for (ContextType context : inputMappings.keys()) {
                if (activeContexts.get(context.ordinal())) {
                    if (inputMappings.get(context).containsKey(input)) {
                        Action action = inputMappings.get(context).get(input);

                        if (consummerMappings.containsKey(context) && consummerMappings.get(context).containsKey(action)) {
                            for (String consummer : consummerMappings.get(context).get(action)) {
                                dispatcher.get(consummer).add(new Event(context, action));
                            }
                        }
                    }
                }
            }
        }
    }

    public Array<Event> pollActions(String consummer) {
        Array<Event> result = new Array<>(dispatcher.get(consummer));
        dispatcher.get(consummer).clear();

        return result;
    }

    public void registerAction(ContextType context, Action action, ContextHandler handler) {
        ObjectMap<Action, Array<ContextHandler>> actionHandlers;
        if (!handlerMappings.containsKey(context)) {
            actionHandlers = new ObjectMap<>();
            handlerMappings.put(context, actionHandlers);
        } else {
            actionHandlers = handlerMappings.get(context);
        }

        Array<ContextHandler> handlers;
        if (!actionHandlers.containsKey(action)) {
            handlers = new Array<>();
            actionHandlers.put(action, handlers);
        } else {
            handlers = actionHandlers.get(action);
        }

        handlers.add(handler);
    }

    public boolean acceptInput(InputMap inputmap) {
        for (Input input : inputmap.getPressed()) {
            if (acceptInput(input)) {
                return true;
            }
        }

        return false;
    }

    public boolean acceptInput(Input input) {
        for (ContextType context : inputMappings.keys()) {
            if (activeContexts.get(context.ordinal())) {
                if (inputMappings.get(context).containsKey(input)) {
                    Action action = inputMappings.get(context).get(input);

                    if (handlerMappings.containsKey(context) && handlerMappings.get(context).containsKey(action)) {
                        for (ContextHandler handler : handlerMappings.get(context).get(action)) {
                            handler.triggerAction(new Event(context, action));
                            return true;
                        }
                    }
                }
            }
        }
        return false;

    }

    public interface ContextHandler {
        public void triggerAction(Event event);
    }
}
