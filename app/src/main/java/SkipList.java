import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class SkipList<K extends Comparable<K>, V> implements Map<K, V> {
    private double p;
    private Node<K, V> head;
    private Node<K, V> tail;
    private int level;
    private int size;
    private Random r;

    public SkipList(double p) {
        this.p = p;
        head = new Node<K, V>(null, null);
        tail = new Node<K, V>(null, null);
        r = new Random();
        clear();
    }

    public SkipList() {
        this(0.5);
    }

    @Override
    public void clear() {
        head.forward.clear();
        head.forward.put(0, tail);
        level = 1;
        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        Node<K, V> cur = head.forward.get(0);
        while (!cur.equals(tail)) {
            if (cur.value.equals(value)) {
                return true;
            }

            cur = cur.forward.get(0);
        }

        return false;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> x = new HashSet<>(size);
        Node<K, V> cur = head.forward.get(0);
        while (!cur.equals(tail)) {
            x.add(new AbstractMap.SimpleImmutableEntry<>(cur.key, cur.value));
            cur = cur.forward.get(0);
        }

        return x;
    }

    @Override
    public V get(Object key) {
        @SuppressWarnings("unchecked")
        K k = (K) key;
        if (k == null) {
            throw new NullPointerException();
        }

        Node<K, V> cur = head;
        for (int i = level - 1; i >= 0; --i) {
            while (!cur.forward.get(i).equals(tail) && cur.forward.get(i).key.compareTo(k) < 0) {
                cur = cur.forward.get(i);
            }
        }

        cur = cur.forward.get(0);
        if (!cur.equals(tail) && cur.key.equals(k)) {
            return cur.value;
        }

        return null;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Set<K> keySet() {
        Set<K> x = new HashSet<>(size);
        Node<K, V> cur = head.forward.get(0);
        while (!cur.equals(tail)) {
            x.add(cur.key);
            cur = cur.forward.get(0);
        }

        return x;
    }

    @Override
    public V put(K key, V value) {
        Map<Integer, Node<K, V>> update = new HashMap<>();
        Node<K, V> cur = head;
        for (int i = level - 1; i >= 0; --i) {
            while (!cur.forward.get(i).equals(tail) && cur.forward.get(i).key.compareTo(key) < 0) {
                cur = cur.forward.get(i);
            }

            update.put(i, cur);
        }

        cur = cur.forward.get(0);
        if (!cur.equals(tail) && cur.key.equals(key)) {
            V prev = cur.value;
            cur.value = value;
            return prev;
        }

        int newLevel = randomLevel();
        if (newLevel > level) {
            for (int i = level; i < newLevel; ++i) {
                update.put(i, head);
                head.forward.put(i, tail);
            }

            level = newLevel;
        }

        cur = new Node<>(key, value);
        for (int i = 0; i < newLevel; ++i) {
            Node<K, V> prev = update.get(i);
            cur.forward.put(i, prev.forward.get(i));
            prev.forward.put(i, cur);
        }

        ++size;
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(Object key) {
        @SuppressWarnings("unchecked")
        K k = (K) key;
        if (k == null) {
            throw new NullPointerException();
        }

        Map<Integer, Node<K, V>> update = new HashMap<>(level);
        Node<K, V> cur = head;
        for (int i = level - 1; i >= 0; --i) {
            while (!cur.forward.get(i).equals(tail) && cur.forward.get(i).key.compareTo(k) < 0) {
                cur = cur.forward.get(i);
            }

            update.put(i, cur);
        }

        cur = cur.forward.get(0);
        if (cur.equals(tail) || !cur.key.equals(k)) {
            return null;
        }

        for (int i = 0; i < level; ++i) {
            Node<K, V> prev = update.get(i);
            if (!prev.forward.get(i).equals(cur)) {
                break;
            }

            prev.forward.put(i, cur.forward.get(i));
        }

        while (level > 1 && head.forward.get(level - 1).equals(tail)) {
            head.forward.remove(level - 1);
            --level;
        }

        --size;
        return cur.value;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Collection<V> values() {
        Collection<V> x = new ArrayList<>(size);
        Node<K, V> cur = head.forward.get(0);
        while (!cur.equals(tail)) {
            x.add(cur.value);
            cur = cur.forward.get(0);
        }

        return x;
    }

    private int randomLevel() {
        int level = 1;
        while (r.nextDouble() < p) {
            ++level;
        }

        return level;
    }

    private static class Node<K extends Comparable<K>, V> {
        private K key;
        private V value;
        private Map<Integer, Node<K, V>> forward;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
            forward = new HashMap<>();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null) {
                return false;
            }

            if (getClass() != o.getClass()) {
                return false;
            }

            @SuppressWarnings("unchecked")
            Node<K, V> x = (Node<K, V>) o;
            return Objects.equals(this.key, x.key) && Objects.equals(this.value, x.value);
        }
    }
}
