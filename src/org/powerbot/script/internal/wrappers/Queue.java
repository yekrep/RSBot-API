package org.powerbot.script.internal.wrappers;

import org.powerbot.client.NodeSub;
import org.powerbot.client.NodeSubQueue;

public class Queue<N extends NodeSub> {
	private final NodeSubQueue nl;
	private NodeSub current;

	public Queue(final NodeSubQueue nl) {
		this.nl = nl;
	}

	public int size() {
		int size = 0;
		NodeSub node = nl.getTail().getNextSub();

		while (node != nl.getTail()) {
			node = node.getNextSub();
			size++;
		}

		return size;
	}

	@SuppressWarnings("unchecked")
	public N getHead() {
		NodeSub node = nl.getTail().getNextSub();

		if (node == nl.getTail()) {
			current = null;
			return null;
		}
		current = node.getNextSub();

		return (N) node;
	}

	@SuppressWarnings("unchecked")
	public N getNext() {
		NodeSub node = current;

		if (node == nl.getTail()) {
			current = null;
			return null;
		}
		current = node.getNextSub();

		return (N) node;
	}
}
