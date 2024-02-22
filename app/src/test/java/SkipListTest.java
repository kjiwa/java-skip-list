import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

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
}
