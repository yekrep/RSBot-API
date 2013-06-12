package org.powerbot.script.internal.wrappers;

import org.powerbot.client.NodeSub;
import org.powerbot.client.NodeSubQueue;

public class Queue<N extends NodeSub> {
	private final NodeSubQueue nl;
	private NodeSub curr;

	public Queue(final NodeSubQueue nl) {
		this.nl = nl;
	}

	public int size() {
		int s = 0;
		NodeSub t = nl.getTail();
		NodeSub n;
		if (t != null) {
			n = t.getNextSub();
		} else {
			n = null;
		}
		while (n != null && n != t) {
			n = n.getNextSub();
			++s;
		}
		return s;
	}

	@SuppressWarnings("unchecked")
	public N getHead() {
		NodeSub t = nl.getTail();
		NodeSub n;
		if (t != null) {
			n = t.getNextSub();
		} else {
			n = null;
		}
		if (n == null || n == t) {
			curr = null;
			return null;
		}
		curr = n.getNextSub();
		return (N) n;
	}

	@SuppressWarnings("unchecked")
	public N getNext() {
		NodeSub t = nl.getTail();
		NodeSub n = curr;
		if (t == null || n == null || n == t) {
			curr = null;
			return null;
		}
		curr = n.getNextSub();
		return (N) n;
	}
}
