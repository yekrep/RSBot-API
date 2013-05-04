package org.powerbot.script.internal.wrappers;

import org.powerbot.client.Node;
import org.powerbot.client.NodeDeque;

public class Deque<N> {
	private final NodeDeque nl;
	private Node current;

	public Deque(final NodeDeque nl) {
		this.nl = nl;
	}

	public int size() {
		int size = 0;
		Node node = nl.getTail().getNext();

		while (node != nl.getTail()) {
			node = node.getNext();
			size++;
		}

		return size;
	}

	@SuppressWarnings("unchecked")
	public N getHead() {
		Node node = nl.getTail().getNext();

		if (node == nl.getTail()) {
			current = null;
			return null;
		}
		current = node.getNext();

		return (N) node;
	}

	@SuppressWarnings("unchecked")
	public N getNext() {
		Node node = current;

		if (node == nl.getTail()) {
			current = null;
			return null;
		}
		current = node.getNext();

		return (N) node;
	}
}
