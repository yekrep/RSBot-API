package org.powerbot.script.internal.wrappers;

import org.powerbot.client.Node;
import org.powerbot.client.NodeDeque;

public class Deque<N> {
	private final NodeDeque nl;
	private Node curr;

	public Deque(final NodeDeque nl) {
		if (nl == null) throw new IllegalArgumentException();
		this.nl = nl;
	}

	public int size() {
		int s = 0;
		Node t = nl.getTail();
		Node n;
		if (t != null) n = t.getNext();
		else n = null;
		while (n != null && n != t) {
			n = n.getNext();
			++s;
		}
		return s;
	}

	@SuppressWarnings("unchecked")
	public N getHead() {
		Node t = nl.getTail();
		Node n;
		if (t != null) n = t.getNext();
		else n = null;
		if (n == null || n == t) {
			curr = null;
			return null;
		}
		curr = n.getNext();
		return (N) n;
	}

	@SuppressWarnings("unchecked")
	public N getNext() {
		Node t = nl.getTail();
		Node n = curr;
		if (t == null || n == null || n == t) {
			curr = null;
			return null;
		}
		curr = n.getNext();
		return (N) n;
	}
}
