import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

class SkipListTest {
    @Test
    void skipListPutSingleEntry() {
        Map<Integer, Integer> x = new SkipList<>();
        assertNull(x.put(1, 2));
        assertEquals(1, x.size());
    }

    @Test
    void skipListPutMultipleEntries() {
        Map<Integer, Integer> x = new SkipList<>();
        for (int i = 0; i < 10; ++i) {
            assertNull(x.put(i, i));
        }

        assertEquals(10, x.size());
    }

    @Test
    void skipListPutReplacesExistingValue() {
        Map<Integer, Integer> x = new SkipList<>();
        assertNull(x.put(1, 2));
        assertEquals(2, x.put(1, 3));
        assertEquals(3, x.get(1));
        assertEquals(1, x.size());
    }

    @Test
    void skipListPutNullKeyThrows() {
        Map<Integer, Integer> x = new SkipList<>();
        assertThrows(NullPointerException.class, () -> x.put(null, 1));
    }

    @Test
    void skipListGetSingleEntry() {
        Map<Integer, Integer> x = new SkipList<>();
        assertNull(x.put(1, 2));
        assertEquals(2, x.get(1));
        assertEquals(1, x.size());
    }

    @Test
    void skipListGetFromEmptyList() {
        Map<Integer, Integer> x = new SkipList<>();
        assertNull(x.get(0));
        assertEquals(0, x.size());
    }

    @Test
    void skipListGetNonexistentKey() {
        Map<Integer, Integer> x = new SkipList<>();
        for (int i = 0; i < 10; ++i) {
            assertNull(x.put(i, i));
        }

        assertNull(x.get(100));
        assertEquals(10, x.size());
    }

    @Test
    void skipListGetNullKeyThrows() {
        Map<Integer, Integer> x = new SkipList<>();
        assertThrows(NullPointerException.class, () -> x.get(null));
    }

    @Test
    void skipListContainsKey() {
        Map<Integer, Integer> x = new SkipList<>();
        x.put(1, 2);
        assertTrue(x.containsKey(1));
        assertFalse(x.containsKey(2));
    }

    @Test
    void skipListContainsValueIncludingNull() {
        Map<Integer, Integer> x = new SkipList<>();
        x.put(1, null);
        x.put(2, 5);
        assertTrue(x.containsValue(null));
        assertTrue(x.containsValue(5));
        assertFalse(x.containsValue(9));
    }

    @Test
    void skipListPutAll() {
        Map<Integer, Integer> src = new TreeMap<>();
        for (int i = 0; i < 10; ++i) {
            src.put(i, i * i);
        }

        Map<Integer, Integer> x = new SkipList<>();
        x.putAll(src);
        assertEquals(10, x.size());
        for (int i = 0; i < 10; ++i) {
            assertEquals(i * i, x.get(i));
        }
    }

    @Test
    void skipListKeySetEntrySetValuesAreOrdered() {
        Map<Integer, Integer> x = new SkipList<>();
        int[] insertOrder = {5, 1, 4, 2, 3};
        for (int k : insertOrder) {
            x.put(k, k * 10);
        }

        assertEquals(List.of(1, 2, 3, 4, 5), new ArrayList<>(x.keySet()));
        assertEquals(List.of(10, 20, 30, 40, 50), new ArrayList<>(x.values()));

        List<Integer> entryKeys = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : x.entrySet()) {
            entryKeys.add(entry.getKey());
        }

        assertEquals(List.of(1, 2, 3, 4, 5), entryKeys);
    }

    @Test
    void skipListRemoveSingleEntry() {
        Map<Integer, Integer> x = new SkipList<>();
        assertNull(x.put(1, 2));
        assertEquals(2, x.remove(1));
        assertEquals(0, x.size());
    }

    @Test
    void skipListRemoveFromMiddleSuccess() {
        Map<Integer, Integer> x = new SkipList<>();
        for (int i = 0; i < 10; ++i) {
            assertNull(x.put(i, i));
        }

        assertEquals(10, x.size());
        assertEquals(5, x.remove(5));
        assertEquals(9, x.size());
    }

    @Test
    void skipListRemoveFromEmptyMap() {
        Map<Integer, Integer> x = new SkipList<>();
        assertNull(x.remove(1));
        assertEquals(0, x.size());
    }

    @Test
    void skipListRemoveNonexistentKey() {
        Map<Integer, Integer> x = new SkipList<>();
        for (int i = 0; i < 10; ++i) {
            assertNull(x.put(i, i));
        }

        assertNull(x.remove(100));
        assertEquals(10, x.size());
    }

    @Test
    void skipListRemoveNullKeyThrows() {
        Map<Integer, Integer> x = new SkipList<>();
        assertThrows(NullPointerException.class, () -> x.remove(null));
    }

    @Test
    void skipListClear() {
        Map<Integer, Integer> x = new SkipList<>();
        for (int i = 0; i < 100; ++i) {
            assertNull(x.put(i, i));
        }

        x.clear();
        assertTrue(x.isEmpty());
        assertEquals(0, x.size());

        for (int i = 0; i < 100; ++i) {
            assertNull(x.put(i, i));
            assertEquals(i, x.get(i));
        }

        assertEquals(100, x.size());
    }

    @Test
    void skipListConstructorRejectsInvalidP() {
        assertThrows(IllegalArgumentException.class, () -> new SkipList<Integer, Integer>(0));
        assertThrows(IllegalArgumentException.class, () -> new SkipList<Integer, Integer>(1));
        assertThrows(IllegalArgumentException.class, () -> new SkipList<Integer, Integer>(-0.5));
    }

    @Test
    void skipListMatchesTreeMapUnderRandomOperations() {
        Map<Integer, Integer> x = new SkipList<>();
        Map<Integer, Integer> reference = new TreeMap<>();
        Random random = new Random(42);

        for (int i = 0; i < 2000; ++i) {
            int key = random.nextInt(200);
            if (random.nextBoolean()) {
                assertEquals(reference.put(key, i), x.put(key, i));
            } else {
                assertEquals(reference.remove(key), x.remove(key));
            }
        }

        assertEquals(reference.size(), x.size());
        assertEquals(new ArrayList<>(reference.keySet()), new ArrayList<>(x.keySet()));
        for (int key : reference.keySet()) {
            assertEquals(reference.get(key), x.get(key));
        }
    }
}
