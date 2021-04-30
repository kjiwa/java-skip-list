package ca.crimsonglow.skiplist;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.*;

public class SkipListTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testClear() {
        Map<Integer, Integer> list = new SkipList<>();
        for (int i = 0; i < 10; ++i) {
            list.put(i, i);
        }

        Assert.assertEquals(10, list.size());
        list.clear();
        Assert.assertTrue(list.isEmpty());
        Assert.assertNull(list.get(1));
    }

    @Test
    public void testContainsKey() {
        Map<Integer, Integer> list = new SkipList<>();
        Assert.assertFalse(list.containsKey(0));
        list.put(0, 0);
        Assert.assertTrue(list.containsKey(0));
        list.remove(0);
        Assert.assertFalse(list.containsKey(0));
    }

    @Test
    public void testContainsValue() {
        Map<Integer, Integer> list = new SkipList<>();
        Assert.assertFalse(list.containsValue(2));
        list.put(0, 2);
        list.put(1, 3);
        Assert.assertTrue(list.containsValue(2));
        list.remove(0);
        Assert.assertFalse(list.containsValue(2));
    }

    @Test
    public void testEntrySet() {
        Map<Integer, Integer> list = new SkipList<>();
        Set<Map.Entry<Integer, Integer>> expected = new HashSet<>();
        for (int i = 0; i < 10; ++i) {
            list.put(i, i);
            expected.add(new AbstractMap.SimpleEntry<>(i, i));
        }

        Assert.assertEquals(expected, list.entrySet());
    }

    @Test
    public void testGet() {
        Map<Integer, Integer> list = new SkipList<>();
        Assert.assertNull(list.get(0));
        list.put(0, 0);
        Assert.assertEquals(Integer.valueOf(0), list.get(0));
    }

    @Test
    public void testIsEmpty() {
        Map<Integer, Integer> list = new SkipList<>();
        Assert.assertTrue(list.isEmpty());
        list.put(1, 1);
        Assert.assertFalse(list.isEmpty());
        list.remove(1);
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void testKeySet() {
        Map<Integer, Integer> list = new SkipList<>();
        Set<Integer> expected = new HashSet<>();
        for (int i = 0; i < 10; ++i) {
            list.put(i, i);
            expected.add(i);
        }

        Assert.assertEquals(expected, list.keySet());
    }

    @Test
    public void testPut() {
        Map<Integer, Integer> list = new SkipList<>();

        Assert.assertTrue(list.isEmpty());
        Assert.assertFalse(list.containsKey(1));
        Assert.assertNull(list.get(1));
        list.put(1, 1);
        Assert.assertTrue(list.containsKey(1));
        Assert.assertEquals(Integer.valueOf(1), list.get(1));
    }

    @Test
    public void testPutAll() {
        Map<Integer, Integer> m = new HashMap<>();
        m.put(1, 2);
        m.put(3, 4);
        m.put(4, 5);

        Map<Integer, Integer> list = new SkipList<>();
        Assert.assertNotEquals(list, m);
        list.putAll(m);
        Assert.assertEquals(m, list);
    }

    @Test
    public void testRemove() {
        Map<Integer, Integer> list = new SkipList<>();

        Assert.assertNull(list.remove(1));
        list.put(1, 1);
        Assert.assertTrue(list.containsKey(1));
        Assert.assertEquals(Integer.valueOf(1), list.remove(1));
        Assert.assertFalse(list.containsKey(1));
        Assert.assertNull(list.get(1));
        Assert.assertNull(list.remove(1));
    }

    @Test
    public void testRemoveNonexistentItemDoesNotChangeSize() {
        Map<Integer, Integer> list = new SkipList<>();
        list.put(1, 1);
        Assert.assertEquals(1, list.size());
        list.remove(0);
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testSize() {
        Map<Integer, Integer> list = new SkipList<>();
        Assert.assertEquals(0, list.size());
        list.put(1, 1);
        Assert.assertEquals(1, list.size());
        list.remove(1);
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void testValues() {
        Map<Integer, Integer> list = new SkipList<>();
        Set<Integer> expected = new HashSet<>();
        for (int i = 0; i < 10; ++i) {
            list.put(i, i + 1);
            expected.add(i + 1);
        }

        Assert.assertEquals(expected, list.values());
    }

    @Test
    public void testEquals() {
        Map<Integer, Integer> m = new HashMap<>();
        m.put(1, 2);
        m.put(3, 4);
        m.put(4, 5);

        Map<Integer, Integer> list = new SkipList<>();
        Assert.assertNotEquals(m, list);
        list.put(1, 2);
        Assert.assertNotEquals(m, list);
        list.put(3, 4);
        Assert.assertNotEquals(m, list);
        list.put(4, 5);
        Assert.assertEquals(m, list);
        list.put(4, 4);
        Assert.assertNotEquals(m, list);
        list.put(6, 7);
        Assert.assertNotEquals(m, list);
    }

    @Test
    public void testNullKeyIsNotAllowed() {
        thrown.expect(NullPointerException.class);
        new SkipList<>().put(null, 10);
    }

    @Test
    public void testNullValueIsAllowed() {
        new SkipList<Integer, Integer>().put(10, null);
    }

    @Test
    public void testExistingValueIsUpdated() {
        Map<Integer, Integer> list = new SkipList<>();

        list.put(1, 1);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(Integer.valueOf(1), list.get(1));

        list.put(1, 2);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(Integer.valueOf(2), list.get(1));
    }
}
