package com.gobs.input;

import com.gobs.ui.Input;
import com.gobs.ui.InputMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ContextManager {
    public enum ContextType {
        GLOBAL, CRAWLING, MAP, EDITMAP, INVENTORY
    }

    public enum Action {
        SHOW_MAP, SHOW_INVENTORY, TOGGLE_EDIT, TOGGLE_VIEW,
        DIG, FILL, TARGET,
        MOVE_LEFT, MOVE_RIGHT, MOVE_UP, MOVE_DOWN,
        SCROLL_LEFT, SCROLL_RIGHT, SCROLL_UP, SCROLL_DOWN,
        ZOOM_IN, ZOOM_OUT,
        EXIT, DUMP
    }

    public class Event {
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
    Map<ContextType, Map<Input, Action>> inputMappings;
    // callback registration for actions
    Map<ContextType, Map<Action, List<ContextHandler>>> handlerMappings;
    // poll based consummer registration for actions
    Map<ContextType, Map<Action, List<String>>> consummerMappings;
    // list of events available for a specific consummer
    Map<String, List<Event>> dispatcher;

    public ContextManager() {
        activeContexts = new BitSet(ContextType.values().length);
        inputMappings = new HashMap<>();
        handlerMappings = new HashMap<>();
        consummerMappings = new HashMap<>();
        dispatcher = new HashMap<>();
    }

    public void activateContext(ContextType context) {
        activeContexts.set(context.ordinal());
    }

    public void disableContext(ContextType context) {
        activeContexts.clear(context.ordinal());
    }

    public void mapInput(ContextType context, Input input, Action action) {
        Map<Input, Action> inputMapping;
        if (!inputMappings.containsKey(context)) {
            inputMapping = new HashMap<>();
            inputMappings.put(context, inputMapping);
        } else {
            inputMapping = inputMappings.get(context);
        }

        inputMapping.put(input, action);
    }

    public void registerConsumer(String consummer, ContextType context, Action action) {
        Map<Action, List<String>> consummerMapping;
        if (!consummerMappings.containsKey(context)) {
            consummerMapping = new HashMap<>();
            consummerMappings.put(context, consummerMapping);
        } else {
            consummerMapping = consummerMappings.get(context);
        }

        if (!consummerMapping.containsKey(action)) {
            consummerMapping.put(action, new ArrayList<>());
        }
        consummerMapping.get(action).add(consummer);
        if (!dispatcher.containsKey(consummer)) {
            dispatcher.put(consummer, new ArrayList<>());
        }
    }

    public void dispatchInput(InputMap inputmap) {
        for (Input input : inputmap.getPressed()) {
            for (ContextType context : inputMappings.keySet()) {
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

    public List<Event> pollActions(String consummer) {
        List<Event> result = new ArrayList<>(dispatcher.get(consummer));
        dispatcher.get(consummer).clear();

        return result;
    }

    public void registerAction(ContextType context, Action action, ContextHandler handler) {
        Map<Action, List<ContextHandler>> actionHandlers;
        if (!handlerMappings.containsKey(context)) {
            actionHandlers = new HashMap<>();
            handlerMappings.put(context, actionHandlers);
        } else {
            actionHandlers = handlerMappings.get(context);
        }

        List<ContextHandler> handlers;
        if (!actionHandlers.containsKey(action)) {
            handlers = new ArrayList<>();
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
        for (ContextType context : inputMappings.keySet()) {
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
