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
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Skip lists are maps that use probabilistic balancing for insertion and
 * deletion algorithms.
 */
public class SkipList<K extends Comparable<K>, V> implements Map<K, V> {
  private class Node {
    public K key;
    public V value;
    public long level;
    public Node next;
    public Node down;

    public Node(K key, V value, long level, Node next, Node down) {
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

  // The default probability to use when selecting a random level.
  private static final double DEFAULT_ITERATION_PROBABILITY = 0.5;

  // The head of the list.
  private Node head;

  // The size of the list.
  private int size;

  // An instance of the random number generator.
  private Random random;

  // The probability with which to continue iterating while selecting a random
  // level.
  private double iterationProbability;

  // Gets the head node at the lowest level in the list.
  private Node getLowestHead() {
    Node cur = head;
    while (cur.down != null) {
      cur = cur.down;
    }

    return cur;
  }

  // Selects a random level by incrementing a counter a random number of times.
  private long getRandomLevel() {
    long level = 0;
    while (level <= size && random.nextDouble() < iterationProbability) {
      level++;
    }

    return level;
  }

  /**
   * Creates a new skip list with default parameters.
   */
  public SkipList() {
    random = new Random();
    iterationProbability = DEFAULT_ITERATION_PROBABILITY;
    clear();
  }

  @Override
  public void clear() {
    head = new Node(null, null, 0, null, null);
    size = 0;
  }

  @Override
  public boolean containsKey(Object key) {
    return get(key) != null;
  }

  @Override
  public boolean containsValue(Object value) {
    Node cur = getLowestHead().next;
    while (cur != null) {
      boolean match = (value == null ? cur.value == null : value.equals(cur.value));
      if (match) {
        return true;
      }
    }

    return false;
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    Set<Entry<K, V>> result = new HashSet<>(size);
    Node cur = getLowestHead().next;
    while (cur != null) {
      result.add(new AbstractMap.SimpleEntry<>(cur.key, cur.value));
      cur = cur.next;
    }

    return result;
  }

  @Override
  public V get(Object key) {
    @SuppressWarnings("unchecked")
    K k = (K) key;
    if (k == null) {
      throw new NullPointerException();
    }

    Node cur = head;
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
  public boolean isEmpty() {
    return size == 0;
  }

  @Override
  public Set<K> keySet() {
    Set<K> result = new HashSet<>(size);
    Node cur = getLowestHead().next;
    while (cur != null) {
      result.add(cur.key);
      cur = cur.next;
    }

    return result;
  }

  @Override
  public V put(K key, V value) {
    if (key == null) {
      throw new NullPointerException();
    }

    long level = getRandomLevel();
    if (level > head.level) {
      head = new Node(null, null, level, null, head);
    }

    Node cur = head;
    Node prevLevelEntry = null;

    while (cur != null) {
      while (cur.isNextKeyLessThan(key)) {
        cur = cur.next;
      }

      // If a node with the key already exists in the list, update the value
      // and do not update the size of the list.
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
      Node n = new Node(key, value, cur.level, cur.next, null);
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

    Node cur = head;
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
  public int size() {
    return size;
  }

  @Override
  public Collection<V> values() {
    Collection<V> result = new HashSet<>(size);
    Node cur = getLowestHead().next;
    while (cur != null) {
      result.add(cur.value);
      cur = cur.next;
    }

    return result;
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
  }
}
