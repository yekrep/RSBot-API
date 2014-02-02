package org.powerbot.os.api.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.powerbot.os.client.Node;
import org.powerbot.os.client.NodeDeque;

public class Deque<N> implements Iterator<N>, Iterable<N> {
	private final NodeDeque deque;
	private final Class<N> type;
	private Node curr;
	private Node next;

	public Deque(final NodeDeque deque, final Class<N> type) {
		if (deque == null || type == null) {
			throw new IllegalArgumentException();
		}
		this.deque = deque;
		this.type = type;
	}

	@Override
	public Iterator<N> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		if (next != null) {
			return true;
		}
		final Node sentinel = deque.getSentinel();
		if (sentinel == null) {
			return false;
		}
		if (curr == null) {
			curr = sentinel;
		}
		final Node n = curr.getNext();
		if (n == null || n == sentinel ||
				!type.isAssignableFrom(n.getClass())) {
			return false;
		}
		next = n;
		return true;
	}

	@Override
	public N next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		final N n = type.cast(next);
		next = null;
		return n;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
