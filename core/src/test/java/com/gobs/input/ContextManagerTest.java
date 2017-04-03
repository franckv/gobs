package com.gobs.input;

import com.gobs.input.ContextManager.Action;
import com.gobs.input.ContextManager.ContextType;
import com.gobs.ui.Input;
import com.gobs.ui.InputMap;
import java.util.List;
import java.util.Map;
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

        Map<Input, Action> inputMapping = manager.inputMappings.get(ContextType.CRAWLING);

        Action action = inputMapping.get(Input.UP);

        Assert.assertEquals(Action.MOVE_UP, action);
    }

    @Test
    public void testRegisterAction() {
        manager.registerAction(ContextType.CRAWLING,
                Action.MOVE_UP, (action) -> {
                    System.out.println("Trigger: " + action);
                    triggered = true;
                });

        Map<Action, List<ContextManager.ContextHandler>> actionHandlers
                = manager.handlerMappings.get(ContextType.CRAWLING);

        List<ContextManager.ContextHandler> handlers = actionHandlers.get(Action.MOVE_UP);

        Assert.assertEquals(1, handlers.size());

        triggered = false;

        handlers.get(0).triggerAction(Action.MOVE_UP);

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
        manager.registerConsumer("test", Action.DUMP);

        Assert.assertEquals(1, manager.consummerMappings.keySet().size());
        Assert.assertEquals(1, manager.consummerMappings.get(Action.DUMP).size());

        Assert.assertEquals("test", manager.consummerMappings.get(Action.DUMP).get(0));

        manager.registerConsumer("test2", Action.DUMP);

        Assert.assertEquals(1, manager.consummerMappings.keySet().size());
        Assert.assertEquals(2, manager.consummerMappings.get(Action.DUMP).size());

        Assert.assertEquals("test", manager.consummerMappings.get(Action.DUMP).get(0));
        Assert.assertEquals("test2", manager.consummerMappings.get(Action.DUMP).get(1));

        manager.registerConsumer("test2", Action.DIG);

        Assert.assertEquals(2, manager.consummerMappings.keySet().size());
        Assert.assertEquals(2, manager.consummerMappings.get(Action.DUMP).size());
        Assert.assertEquals(1, manager.consummerMappings.get(Action.DIG).size());

        Assert.assertEquals("test", manager.consummerMappings.get(Action.DUMP).get(0));
        Assert.assertEquals("test2", manager.consummerMappings.get(Action.DUMP).get(1));
        Assert.assertEquals("test2", manager.consummerMappings.get(Action.DIG).get(0));
    }

    @Test
    public void testDispatchInput() {
        manager.activateContext(ContextManager.ContextType.CRAWLING);
        manager.mapInput(ContextType.CRAWLING, Input.D, Action.DUMP);
        manager.mapInput(ContextType.CRAWLING, Input.ESCAPE, Action.EXIT);
        manager.registerConsumer("test", Action.DUMP);
        manager.registerConsumer("test", Action.EXIT);
        manager.registerConsumer("test2", Action.EXIT);

        InputMap map = new InputMap();
        map.set(Input.UP);
        manager.dispatchInput(map);

        Assert.assertEquals(0, manager.dispatcher.get("test").size());

        map.set(Input.D);
        manager.dispatchInput(map);

        Assert.assertEquals(1, manager.dispatcher.get("test").size());

        map.clear(Input.UP);
        map.set(Input.ESCAPE);
        manager.dispatchInput(map);

        Assert.assertEquals(2, manager.dispatcher.get("test").size());
        Assert.assertEquals(1, manager.dispatcher.get("test2").size());

        map.clear(Input.D);
        manager.dispatchInput(map);

        Assert.assertEquals(1, manager.dispatcher.get("test").size());
        Assert.assertEquals(Action.EXIT, manager.dispatcher.get("test").get(0));
        Assert.assertEquals(1, manager.dispatcher.get("test2").size());
        Assert.assertEquals(Action.EXIT, manager.dispatcher.get("test2").get(0));
    }
}
