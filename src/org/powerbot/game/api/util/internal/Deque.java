package org.powerbot.game.api.util.internal;

import org.powerbot.game.client.Node;
import org.powerbot.game.client.NodeDequeTail;

public class Deque<N> {
	private final Object nl;
	private Node current;

	public Deque(final Object nl) {
		this.nl = nl;
	}

	public int size() {
		int size = 0;
		Node node = ((Node) ((NodeDequeTail) nl).getNodeDequeTail()).getPrevious();

		while (node != ((NodeDequeTail) nl).getNodeDequeTail()) {
			node = node.getPrevious();
			size++;
		}

		return size;
	}

	public N getHead() {
		final Node node = ((Node) ((NodeDequeTail) nl).getNodeDequeTail()).getNext();

		if (node == ((NodeDequeTail) nl).getNodeDequeTail()) {
			current = null;
			return null;
		}
		current = node.getNext();

		return (N) node;
	}

	public N getTail() {
		final Node node = ((Node) ((NodeDequeTail) nl).getNodeDequeTail()).getPrevious();

		if (node == ((NodeDequeTail) nl).getNodeDequeTail()) {
			current = null;
			return null;
		}
		current = node.getPrevious();

		return (N) node;
	}

	public N getNext() {
		Node node = current;

		if (node == ((NodeDequeTail) nl).getNodeDequeTail()) {
			current = null;
			return null;
		}
		current = node.getNext();

		return (N) node;
	}
}
