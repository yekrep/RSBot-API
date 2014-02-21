package org.powerbot.script.internal.wrappers;

import org.powerbot.bot.client.NodeSub;
import org.powerbot.bot.client.NodeSubQueue;

public class Queue<N extends NodeSub> {
	private final NodeSubQueue nl;
	private final Class<N> type;
	private NodeSub curr;

	public Queue(final NodeSubQueue nl, final Class<N> type) {
		this.nl = nl;
		this.type = type;
	}

	public int size() {
		int s = 0;
		final NodeSub t = nl.getTail();
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

	public N getHead() {
		final NodeSub t = nl.getTail();
		final NodeSub n;
		if (t != null) {
			n = t.getNextSub();
		} else {
			n = null;
		}
		if (n == null || n == t || !type.isInstance(n)) {
			curr = null;
			return null;
		}
		curr = n.getNextSub();
		return type.cast(n);
	}

	public N getNext() {
		final NodeSub t = nl.getTail();
		final NodeSub n = curr;
		if (t == null || n == null || n == t || !type.isInstance(n)) {
			curr = null;
			return null;
		}
		curr = n.getNextSub();
		return type.cast(n);
	}
}
