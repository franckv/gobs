package com.gobs.util;

import com.badlogic.gdx.utils.Array;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PointQuadTreeTest {
    PointQuadTree<Integer> tree;

    @Before
    public void setUp() {
        tree = new PointQuadTree<>(10, 0, 0, 10);
    }

    @Test
    public void testInsert() {
        tree.insert(42, 5, 5);
        tree.insert(13, 3, 5);

        Array<Integer> r = tree.find(0, 0);

        Assert.assertNull(r);

        r = tree.find(5, 5);

        Assert.assertEquals(1, r.size);
        Assert.assertTrue(r.get(0) == 42);
    }
}
