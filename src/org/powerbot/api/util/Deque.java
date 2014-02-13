package org.powerbot.api.util;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.powerbot.bot.client.Node;
import org.powerbot.bot.client.NodeDeque;

public class Deque<N> implements Iterator<N>, Iterable<N> {
	private final WeakReference<NodeDeque> deque;
	private final Class<N> type;
	private Node curr;
	private Node next;

	public Deque(final NodeDeque deque, final Class<N> type) {
		if (type == null) {
			throw new IllegalArgumentException();
		}
		this.deque = new WeakReference<NodeDeque>(deque);
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
		final NodeDeque deque = this.deque.get();
		final Node sentinel = deque != null ? deque.getSentinel() : null;
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
