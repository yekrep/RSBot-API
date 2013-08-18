package org.powerbot.script.internal.wrappers;

import org.powerbot.client.Node;
import org.powerbot.client.NodeDeque;

public class Deque<N> {
	private final NodeDeque nl;
	private final Class<N> type;
	private Node curr;

	public Deque(NodeDeque nl, Class<N> type) {
		if (nl == null) {
			throw new IllegalArgumentException();
		}
		this.nl = nl;
		this.type = type;
	}

	public int size() {
		int s = 0;
		Node t = nl.getTail();
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
		Node t = nl.getTail();
		Node n;
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
		Node t = nl.getTail();
		Node n = curr;
		if (t == null || n == null || n == t || !type.isInstance(n)) {
			curr = null;
			return null;
		}
		curr = n.getNext();
		return type.cast(n);
	}
}
