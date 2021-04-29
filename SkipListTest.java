/*
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SkipListTest
{
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
    Assert.assertFalse(list.containsValue(null));
    list.put(0, null);
    Assert.assertTrue(list.containsValue(null));
    list.remove(0);
    Assert.assertFalse(list.containsValue(null));
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
  public void testRemoveNonexistantItemDoesNotChangeSize() {
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
    Map<Integer, Integer> list = new SkipList<>();
    list.put(null, 10);
  }

  @Test
  public void testNullValueIsAllowed() {
    SkipList<Integer, Integer> list = new SkipList<>();
    list.put(10, null);
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
