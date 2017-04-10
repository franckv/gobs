package com.gobs.input;

import com.gobs.input.ContextManager.Action;
import com.gobs.input.ContextManager.ContextType;
import com.gobs.input.ContextManager.Event;
import com.gobs.input.InputMap.Input;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class ContextManagerTest {
    ContextManager manager;
    boolean triggered;

    @Before
    public void setUp() {
        manager = new ContextManager();
        triggered = false;
    }

    @Test
    public void testMapInput() {
        manager.mapInput(ContextType.CRAWLING, Input.UP, Action.MOVE_UP);

        ObjectMap<Input, Action> inputMapping = manager.inputMappings.get(ContextType.CRAWLING);

        Action action = inputMapping.get(Input.UP);

        Assert.assertEquals(Action.MOVE_UP, action);
    }

    @Test
    public void testRegisterAction() {
        manager.registerAction(ContextType.CRAWLING,
                Action.MOVE_UP, (event) -> {
                    System.out.println("Trigger: " + event);
                    triggered = true;
                });

        ObjectMap<Action, Array<ContextManager.ContextHandler>> actionHandlers
                = manager.handlerMappings.get(ContextType.CRAWLING);

        Array<ContextManager.ContextHandler> handlers = actionHandlers.get(Action.MOVE_UP);

        Assert.assertEquals(1, handlers.size);

        triggered = false;

        Event event = new Event(ContextType.CRAWLING, Action.MOVE_UP);

        handlers.get(0).triggerAction(event);

        Assert.assertTrue(triggered);
    }

    @Test
    public void testAcceptInput() {
        manager.activateContext(ContextManager.ContextType.CRAWLING);

        InputMap map = new InputMap();
        map.set(Input.UP);

        manager.mapInput(ContextType.CRAWLING, Input.UP, Action.MOVE_UP);

        boolean accepted = manager.acceptInput(map);

        Assert.assertFalse(triggered || accepted);

        manager.registerAction(ContextType.CRAWLING,
                Action.MOVE_UP, (action) -> {
                    System.out.println("Trigger: " + action);
                    triggered = true;
                });

        accepted = manager.acceptInput(map);

        Assert.assertTrue(triggered && accepted);
    }

    @Test
    public void testRegisterConsumer() {
        manager.registerConsumer("test", ContextType.CRAWLING, Action.DUMP);

        Assert.assertEquals(1, manager.consummerMappings.get(ContextType.CRAWLING).keys().toArray().size);
        Assert.assertEquals(1, manager.consummerMappings.get(ContextType.CRAWLING).get(Action.DUMP).size);

        Assert.assertEquals("test", manager.consummerMappings.get(ContextType.CRAWLING).get(Action.DUMP).get(0));

        manager.registerConsumer("test2", ContextType.CRAWLING, Action.DUMP);

        Assert.assertEquals(1, manager.consummerMappings.get(ContextType.CRAWLING).keys().toArray().size);
        Assert.assertEquals(2, manager.consummerMappings.get(ContextType.CRAWLING).get(Action.DUMP).size);

        Assert.assertEquals("test", manager.consummerMappings.get(ContextType.CRAWLING).get(Action.DUMP).get(0));
        Assert.assertEquals("test2", manager.consummerMappings.get(ContextType.CRAWLING).get(Action.DUMP).get(1));

        manager.registerConsumer("test2", ContextType.CRAWLING, Action.DIG);

        Assert.assertEquals(2, manager.consummerMappings.get(ContextType.CRAWLING).keys().toArray().size);
        Assert.assertEquals(2, manager.consummerMappings.get(ContextType.CRAWLING).get(Action.DUMP).size);
        Assert.assertEquals(1, manager.consummerMappings.get(ContextType.CRAWLING).get(Action.DIG).size);

        Assert.assertEquals("test", manager.consummerMappings.get(ContextType.CRAWLING).get(Action.DUMP).get(0));
        Assert.assertEquals("test2", manager.consummerMappings.get(ContextType.CRAWLING).get(Action.DUMP).get(1));
        Assert.assertEquals("test2", manager.consummerMappings.get(ContextType.CRAWLING).get(Action.DIG).get(0));
    }

    @Test
    public void testDispatchInput() {
        manager.activateContext(ContextType.CRAWLING);
        manager.mapInput(ContextType.CRAWLING, Input.D, Action.DUMP);
        manager.mapInput(ContextType.CRAWLING, Input.ESCAPE, Action.EXIT);
        manager.registerConsumer("test", ContextType.CRAWLING, Action.DUMP);
        manager.registerConsumer("test", ContextType.CRAWLING, Action.EXIT);
        manager.registerConsumer("test2", ContextType.CRAWLING, Action.EXIT);

        InputMap map = new InputMap();
        map.set(Input.UP);
        manager.dispatchInput(map);

        Assert.assertEquals(0, manager.dispatcher.get("test").size);

        map.set(Input.D);
        manager.dispatchInput(map);

        Assert.assertEquals(1, manager.dispatcher.get("test").size);

        manager.pollActions("test");

        map.clear(Input.UP);
        map.set(Input.ESCAPE);
        manager.dispatchInput(map);

        Assert.assertEquals(2, manager.dispatcher.get("test").size);
        Assert.assertEquals(1, manager.dispatcher.get("test2").size);

        manager.pollActions("test");
        manager.pollActions("test2");

        map.clear(Input.D);
        manager.dispatchInput(map);

        Assert.assertEquals(1, manager.dispatcher.get("test").size);
        Assert.assertEquals(Action.EXIT, manager.dispatcher.get("test").get(0).getAction());
        Assert.assertEquals(1, manager.dispatcher.get("test2").size);
        Assert.assertEquals(Action.EXIT, manager.dispatcher.get("test2").get(0).getAction());
    }
}
