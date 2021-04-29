import java.util.Map;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SkipListTest
{
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testAddItemToList() {
    Map<Integer, Integer> list = new SkipList<>();

    Assert.assertEquals(0, list.size());
    Assert.assertNull(list.get(1));
    Assert.assertNull(list.remove(1));

    list.put(1, 1);
    Assert.assertEquals(1, list.size());
    Assert.assertEquals(Integer.valueOf(1), list.get(1));
    Assert.assertEquals(Integer.valueOf(1), list.remove(1));
  }

  @Test
  public void testRemoveItemFromList() {
    Map<Integer, Integer> list = new SkipList<>();

    list.put(1, 1);
    Assert.assertEquals(1, list.size());
    Assert.assertEquals(Integer.valueOf(1), list.get(1));

    Assert.assertEquals(Integer.valueOf(1), list.remove(1));
    Assert.assertEquals(0, list.size());
    Assert.assertNull(list.get(1));
    Assert.assertNull(list.remove(1));
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
    SkipList<Integer, Integer> list = new SkipList<>();

    list.put(1, 1);
    Assert.assertEquals(1, list.size());
    Assert.assertEquals(Integer.valueOf(1), list.get(1));

    list.put(1, 2);
    Assert.assertEquals(1, list.size());
    Assert.assertEquals(Integer.valueOf(2), list.get(1));
  }
}
