import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

/**
 * A probabilistically balanced {@link java.util.Map} ordered by key, as described in Pugh's
 * "Skip Lists: A Probabilistic Alternative to Balanced Trees". Keys must be non-null and mutually
 * comparable. Iteration order is ascending by key. Not thread-safe.
 */
public class SkipList<K extends Comparable<K>, V> extends AbstractMap<K, V> {
    private static final int MAX_LEVEL = 32;

    private final double p;
    private final Node<K, V> head;
    private final Node<K, V> tail;
    private final Random r;
    private int level;
    private int size;

    public SkipList(double p) {
        if (!(p > 0 && p < 1)) {
            throw new IllegalArgumentException("p must be in (0, 1)");
        }

        this.p = p;
        head = new Node<>(null, null);
        tail = new Node<>(null, null);
        r = new Random();
        clear();
    }

    public SkipList() {
        this(0.5);
    }

    @Override
    public void clear() {
        head.forward.clear();
        head.forward.add(tail);
        level = 1;
        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return findNode(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        Node<K, V> cur = head.forward.get(0);
        while (cur != tail) {
            if (Objects.equals(cur.value, value)) {
                return true;
            }

            cur = cur.forward.get(0);
        }

        return false;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> x = new LinkedHashSet<>(size);
        Node<K, V> cur = head.forward.get(0);
        while (cur != tail) {
            x.add(new SimpleImmutableEntry<>(cur.key, cur.value));
            cur = cur.forward.get(0);
        }

        return x;
    }

    @Override
    public V get(Object key) {
        Node<K, V> node = findNode(key);
        return node == null ? null : node.value;
    }

    @Override
    public V put(K key, V value) {
        Objects.requireNonNull(key);
        List<Node<K, V>> update = new ArrayList<>(Collections.nCopies(level, head));
        Node<K, V> cur = head;
        for (int i = level - 1; i >= 0; --i) {
            while (cur.forward.get(i) != tail && cur.forward.get(i).key.compareTo(key) < 0) {
                cur = cur.forward.get(i);
            }

            update.set(i, cur);
        }

        cur = cur.forward.get(0);
        if (cur != tail && cur.key.equals(key)) {
            V prev = cur.value;
            cur.value = value;
            return prev;
        }

        int newLevel = randomLevel();
        if (newLevel > level) {
            for (int i = level; i < newLevel; ++i) {
                update.add(head);
                head.forward.add(tail);
            }

            level = newLevel;
        }

        cur = new Node<>(key, value);
        for (int i = 0; i < newLevel; ++i) {
            Node<K, V> prev = update.get(i);
            cur.forward.add(prev.forward.get(i));
            prev.forward.set(i, cur);
        }

        ++size;
        return null;
    }

    @Override
    public V remove(Object key) {
        @SuppressWarnings("unchecked")
        K k = (K) Objects.requireNonNull(key);
        List<Node<K, V>> update = new ArrayList<>(Collections.nCopies(level, head));
        Node<K, V> cur = head;
        for (int i = level - 1; i >= 0; --i) {
            while (cur.forward.get(i) != tail && cur.forward.get(i).key.compareTo(k) < 0) {
                cur = cur.forward.get(i);
            }

            update.set(i, cur);
        }

        cur = cur.forward.get(0);
        if (cur == tail || !cur.key.equals(k)) {
            return null;
        }

        for (int i = 0; i < level; ++i) {
            Node<K, V> prev = update.get(i);
            if (prev.forward.get(i) != cur) {
                break;
            }

            prev.forward.set(i, cur.forward.get(i));
        }

        while (level > 1 && head.forward.get(level - 1) == tail) {
            head.forward.remove(level - 1);
            --level;
        }

        --size;
        return cur.value;
    }

    @Override
    public int size() {
        return size;
    }

    /** Finds the node for {@code key}, or {@code null} if absent. */
    private Node<K, V> findNode(Object key) {
        @SuppressWarnings("unchecked")
        K k = (K) Objects.requireNonNull(key);
        Node<K, V> cur = head;
        for (int i = level - 1; i >= 0; --i) {
            while (cur.forward.get(i) != tail && cur.forward.get(i).key.compareTo(k) < 0) {
                cur = cur.forward.get(i);
            }
        }

        cur = cur.forward.get(0);
        return cur != tail && cur.key.equals(k) ? cur : null;
    }

    private int randomLevel() {
        int lvl = 1;
        while (lvl < MAX_LEVEL && r.nextDouble() < p) {
            ++lvl;
        }

        return lvl;
    }

    private static class Node<K extends Comparable<K>, V> {
        private final K key;
        private final List<Node<K, V>> forward;
        private V value;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            forward = new ArrayList<>();
        }
    }
}
