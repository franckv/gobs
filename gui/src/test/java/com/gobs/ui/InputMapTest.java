package com.gobs.ui;

import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class InputMapTest {
    InputMap map;

    @Before
    public void setUp() {
        map = new InputMap();
    }

    @Test
    public void testSet() {
        Assert.assertFalse(map.isActive(Input.LEFT));
        map.set(Input.LEFT);
        Assert.assertTrue(map.isActive(Input.LEFT));
        Assert.assertEquals(1, map.active.cardinality());
        
        Assert.assertFalse(map.isActive(Input.E));
        map.set(Input.E);
        Assert.assertTrue(map.isActive(Input.E));
        Assert.assertFalse(map.isActive(Input.F));
        Assert.assertEquals(2, map.active.cardinality());
    }

    @Test
    public void testGet() {
        map = new InputMap();

        List<Input> inputs = Arrays.asList(new Input[]{
            Input.LEFT, Input.A, Input.TAB, Input.Z
        });

        for (Input input : inputs) {
            map.set(input);
        }

        for (Input input : map.getActive()) {
            Assert.assertEquals(map.getActive().size(), inputs.size());
            Assert.assertTrue(inputs.contains(input));
        }
    }
}
