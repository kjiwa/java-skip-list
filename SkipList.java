import java.util.Random;

public class SkipList<T extends Comparable<T>, U>
{
	private class Node
	{
		public T key;
		public U value;
		public long level;
		public Node next;
		public Node down;
		
		public Node(T key, U value, long level, Node next, Node down)
		{
			this.key = key;
			this.value = value;
			this.level = level;
			this.next = next;
			this.down = down;
		}
	}
	
	private Node _head;
	private Random _random;
	private long _size;
	private double _p;
	
	private long _level()
	{
		long level = 0;
		while (level <= _size && _random.nextDouble() < _p) {
			level++;
		}
		
		return level;
	}
	
	public SkipList()
	{
		_head = new Node(null, null, 0, null, null);
		_random = new Random();
		_size = 0;
		_p = 0.5;
	}
	
	public void add(T key, U value)
	{
		long level = _level();
		if (level > _head.level) {
			_head = new Node(null, null, level, null, _head);
		}
		
		Node cur = _head;
		Node last = null;
		
		while (cur != null) {
			if (cur.next == null || cur.next.key.compareTo(key) > 0) {
				if (level >= cur.level) {
					Node n = new Node(key, value, cur.level, cur.next, null);
					if (last != null) {
						last.down = n;
					}
					
					cur.next = n;
					last = n;
				}
				
				cur = cur.down;
				continue;
			} else if (cur.next.key.equals(key)) {
				cur.next.value = value;
				return;
			}
			
			cur = cur.next;
		}
		
		_size++;
	}
	
	public boolean containsKey(T key)
	{
		return get(key) != null;
	}
	
	public U remove(T key)
	{
		U value = null;
		
		Node cur = _head;
		while (cur != null) {
			if (cur.next == null || cur.next.key.compareTo(key) >= 0) {
				if (cur.next != null && cur.next.key.equals(key)) {
					value = cur.next.value;
					cur.next = cur.next.next;
				}
				
				cur = cur.down;
				continue;
			}
			
			cur = cur.next;
		}
		
		_size--;
		return value;
	}
	
	public U get(T key)
	{
		Node cur = _head;
		while (cur != null) {
			if (cur.next == null || cur.next.key.compareTo(key) > 0) {
				cur = cur.down;
				continue;
			} else if (cur.next.key.equals(key)) {
				return cur.next.value;
			}
			
			cur = cur.next;
		}
		
		return null;
	}
}
