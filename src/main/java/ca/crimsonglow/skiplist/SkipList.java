package ca.crimsonglow.skiplist;

import java.util.*;

/**
 * Skip lists are maps that use probabilistic balancing for insertion and deletion algorithms.
 */
public class SkipList<K extends Comparable<K>, V> implements Map<K, V> {
    // The default probability to use when selecting a random level.
    private static final double DEFAULT_ITERATION_PROBABILITY = 0.2;
    // An instance of the random number generator.
    private final Random random;
    // The probability with which to continue iterating while selecting a level.
    private final double iterationProbability;
    // The head of the list.
    private Node<K, V> head;
    // The size of the list.
    private int size;

    /**
     * Creates a new skip list with default parameters.
     */
    public SkipList() {
        this(DEFAULT_ITERATION_PROBABILITY);
    }

    /**
     * Creates a new skip list with the specified iteration probability.
     *
     * @param iterationProbability The probability with which to continue iterating during level selection.
     */
    public SkipList(double iterationProbability) {
        random = new Random();
        this.iterationProbability = iterationProbability;
        clear();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        Node<K, V> cur = getLowestHead().next;
        while (cur != null) {
            boolean match = Objects.equals(value, cur.value);
            if (match) {
                return true;
            }

            cur = cur.next;
        }

        return false;
    }

    @Override
    public V get(Object key) {
        @SuppressWarnings("unchecked")
        K k = (K) key;
        if (k == null) {
            throw new NullPointerException();
        }

        Node<K, V> cur = head;
        while (cur != null) {
            while (cur.isNextKeyLessThan(k)) {
                cur = cur.next;
            }

            if (cur.isNextKeyEqualTo(k)) {
                return cur.next.value;
            }

            cur = cur.down;
        }

        return null;
    }

    @Override
    public V put(K key, V value) {
        if (key == null) {
            throw new NullPointerException();
        }

        long level = getRandomLevel();
        if (level > head.level) {
            head = new Node<>(null, null, level, null, head);
        }

        Node<K, V> cur = head;
        Node<K, V> prevLevelEntry = null;

        while (cur != null) {
            while (cur.isNextKeyLessThan(key)) {
                cur = cur.next;
            }

            // If a node with the key already exists in the list, update the
            if (cur.isNextKeyEqualTo(key)) {
                V prevValue = cur.next.value;
                cur.next.value = value;
                return prevValue;
            }

            // Move down if we are not at or beneath the selected level.
            if (level < cur.level) {
                cur = cur.down;
                continue;
            }

            // Insert a new node in the list.
            Node<K, V> n = new Node<>(key, value, cur.level, cur.next, null);
            if (prevLevelEntry != null) {
                prevLevelEntry.down = n;
            }

            prevLevelEntry = n;
            cur.next = n;
            cur = cur.down;
        }

        size++;
        return null;
    }

    @Override
    public V remove(Object key) {
        @SuppressWarnings("unchecked")
        K k = (K) key;
        if (k == null) {
            throw new NullPointerException();
        }

        Node<K, V> cur = head;
        boolean found = false;
        V value = null;

        while (cur != null) {
            while (cur.isNextKeyLessThan(k)) {
                cur = cur.next;
            }

            if (cur.isNextKeyEqualTo(k)) {
                found = true;
                value = cur.next.value;
                cur.next = cur.next.next;
            }

            cur = cur.down;
        }

        if (found) {
            size--;
        }

        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        head = new Node<>(null, null, 0, null, null);
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        Set<K> result = new HashSet<>(size);
        Node<K, V> cur = getLowestHead().next;
        while (cur != null) {
            result.add(cur.key);
            cur = cur.next;
        }

        return result;
    }

    @Override
    public Collection<V> values() {
        Collection<V> result = new HashSet<>(size);
        Node<K, V> cur = getLowestHead().next;
        while (cur != null) {
            result.add(cur.value);
            cur = cur.next;
        }

        return result;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> result = new HashSet<>(size);
        Node<K, V> cur = getLowestHead().next;
        while (cur != null) {
            result.add(new AbstractMap.SimpleEntry<>(cur.key, cur.value));
            cur = cur.next;
        }

        return result;
    }

    private long getRandomLevel() {
        long level = 0;
        while (level <= size && random.nextDouble() < iterationProbability) {
            level++;
        }

        return level;
    }

    // Gets the head node at the lowest level in the list.
    private Node<K, V> getLowestHead() {
        Node<K, V> cur = head;
        while (cur.down != null) {
            cur = cur.down;
        }

        return cur;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof SkipList)) {
            return false;
        }

        SkipList<?, ?> other = (SkipList<?, ?>) o;
        return entrySet().equals(other.entrySet());
    }    // Selects a random level by incrementing a counter a random number of times.

    private static class Node<K extends Comparable<K>, V> {
        public K key;
        public V value;
        public long level;
        public Node<K, V> next;
        public Node<K, V> down;

        public Node(K key, V value, long level, Node<K, V> next, Node<K, V> down) {
            this.key = key;
            this.value = value;
            this.level = level;
            this.next = next;
            this.down = down;
        }

        public boolean isNextKeyLessThan(K key) {
            return (next != null && next.key.compareTo(key) < 0);
        }

        public boolean isNextKeyEqualTo(K key) {
            return (next != null && next.key.equals(key));
        }
    }


}
