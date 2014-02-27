package org.powerbot.script.internal.wrappers;

import org.powerbot.bot.rs3.client.Node;
import org.powerbot.bot.rs3.client.NodeDeque;

public class Deque<N> {
	private final NodeDeque nl;
	private final Class<N> type;
	private Node curr;

	public Deque(final NodeDeque nl, final Class<N> type) {
		if (nl == null) {
			throw new IllegalArgumentException();
		}
		this.nl = nl;
		this.type = type;
	}

	public int size() {
		int s = 0;
		final Node t = nl.getTail();
		Node n;
		if (t != null) {
			n = t.getNext();
		} else {
			n = null;
		}
		while (n != null && n != t) {
			n = n.getNext();
			++s;
		}
		return s;
	}

	public N getHead() {
		final Node t = nl.getTail();
		final Node n;
		if (t != null) {
			n = t.getNext();
		} else {
			n = null;
		}
		if (n == null || n == t || !type.isInstance(n)) {
			curr = null;
			return null;
		}
		curr = n.getNext();
		return type.cast(n);
	}

	public N getNext() {
		final Node t = nl.getTail();
		final Node n = curr;
		if (t == null || n == null || n == t || !type.isInstance(n)) {
			curr = null;
			return null;
		}
		curr = n.getNext();
		return type.cast(n);
	}
}
