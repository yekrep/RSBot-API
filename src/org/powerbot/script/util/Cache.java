package org.powerbot.script.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A very minimal size-eviction based cache implementation.
 * Entries are evicted in a least-recently-used manner.
 *
 * @param <K> key type
 * @param <V> value type
 */
@SuppressWarnings("unchecked")
public class Cache<K, V> implements Map<K, V> {//TODO concurrent (thread-safe)
	private final Map<K, Node<K, V>> table = new HashMap<>();
	private final Queue<K, V> history = new Queue<>();
	private final Lock lock = new ReentrantLock();
	private final int size;
	private int space;

	/**
	 * Creates a new cache with no limit on size.
	 */
	public Cache() {
		this(-1);
	}

	/**
	 * Instantiates a new {@code Cache} with a maximum size (weight).
	 *
	 * @param space the maximum weight of this {@link Cache}
	 */
	public Cache(final int space) {
		this.size = space;
		this.space = space;
	}

	/**
	 * Returns the current size of this {@link Cache}.
	 *
	 * @return the size of the cache
	 */
	@Override
	public int size() {
		return table.size();
	}

	/**
	 * Determines if the {@link Cache} is empty or not.
	 *
	 * @return <tt>true</tt> if empty; otherwise <tt>false</tt>
	 */
	@Override
	public boolean isEmpty() {
		return table.isEmpty();
	}

	/**
	 * Determines if a key exists within this {@link Cache}.
	 *
	 * @param key the key to search for
	 * @return <tt>true</tt> if the key is in the cache; otherwise <tt>false</tt>
	 */
	@Override
	public boolean containsKey(final Object key) {
		lock.lock();
		try {
			return table.containsKey(key);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Unsupported operation
	 */
	@Override
	public boolean containsValue(final Object value) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the value for a given key if it exists in the {@link Cache}.  Otherwise {@code null}.
	 * <p/>
	 * Resets position in eviction queue.
	 *
	 * @param key the key to search for
	 * @return the value of the key in the cache
	 */
	@Override
	public V get(final Object key) {
		lock.lock();
		try {
			final Node<K, V> node = table.get(key);
			if (node == null) {
				return null;
			}
			history.add(node);
			return node.value;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Inserts a key into this cache with the specified value.  Returns the old value for the key,
	 * or {@code null} if there was none.
	 * <p/>
	 * Performs eviction on entries if required.
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	@Override
	public V put(K key, V value) {
		lock.lock();
		try {
			final Node<K, V> cached = table.get(key);
			if (cached != null) {
				final V v = cached.value;
				cached.value = value;
				history.add(cached);
				return v;
			}
			if (space == 0) {
				final Node<K, V> h = history.poll();
				table.remove(h.key);
			} else {
				space--;
			}
			final Node<K, V> e = new Node<>(key, value);
			table.put(key, e);
			history.add(e);
			return null;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Removes a key from the {@link Cache}.  Returns the value if there was one; otherwise {@code null}.
	 *
	 * @param key the key to remove
	 * @return the removed value of the given key
	 */
	@Override
	public V remove(Object key) {
		lock.lock();
		try {
			final Node<K, V> node = table.remove(key);
			if (node == null) {
				return null;
			}
			space++;
			node.pop();
			return node.value;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Unsupported operation
	 */
	@Override
	public void putAll(final Map<? extends K, ? extends V> m) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Removes all entries from this {@link Cache}.
	 */
	@Override
	public void clear() {
		lock.lock();
		try {
			table.clear();
			while (history.poll() != null) {
				;
			}
			space = size;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Unsupported operation
	 */
	@Override
	public Set<K> keySet() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported operation
	 */
	@Override
	public Collection<V> values() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported operation
	 */
	@Override
	public Set<Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}

	private static class Queue<K, V> {
		/*  End(s) queue */
		private final Node<K, V> nil = new Node<>();

		public Queue() {
			nil.next = nil;
			nil.prev = nil;
		}

		public void add(final Node<K, V> node) {
			/* Remove node */
			if (node.prev != null) {
				node.pop();
			}
			/* Set node's previous to the end (previous) */
			node.prev = nil.prev;
			/* Set node's next to end */
			node.next = nil;
			/* Position in nodes */
			node.prev.next = node;
			node.next.prev = node;
		}

		public Node poll() {
			/* Get end's next (first) */
			final Node node = nil.next;
			/* Check if end */
			if (node == nil) {
				return null;
			}
			/* Remove node */
			node.pop();
			return node;
		}
	}

	private static class Node<K, V> {
		public K key;
		public V value;
		Node<K, V> prev;
		Node<K, V> next;

		public Node() {
			/* Dead node */
		}

		public Node(final K paramK, final V paramV) {
			this.key = paramK;
			this.value = paramV;
		}

		public void pop() {
			/* Check if referencing another node */
			if (prev == null) {
				return;
			}
			/* Set previous next to this next */
			prev.next = next;
			/* Set next previous to this previous */
			next.prev = prev;
			/* Null references */
			next = null;
			prev = null;
		}
	}
}
