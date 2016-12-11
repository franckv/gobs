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
}
