package org.powerbot.bot.rt6;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.bot.rt6.client.Node;
import org.powerbot.bot.rt6.client.NodeDeque;
import org.powerbot.bot.rt6.client.NodeSub;
import org.powerbot.bot.rt6.client.NodeSubQueue;

public class NodeQueue {
	public static <E> List<E> get(final NodeDeque q, final Class<E> type) {
		final List<E> list = new ArrayList<E>();
		Node e;

		if (q == null || (e = q.getTail()) == null) {
			return list;
		}
		e = e.getNext();

		for (; e != null && type.isInstance(e); e = e.getNext()) {
			list.add(type.cast(e));
		}

		return list;
	}

	public static <E extends NodeSub> List<E> get(final NodeSubQueue q, final Class<E> type) {
		final List<E> list = new ArrayList<E>();
		NodeSub e;

		if (q == null || (e = q.getTail()) == null) {
			return list;
		}
		e = e.getNextSub();

		for (; e != null && type.isInstance(e); e = e.getNextSub()) {
			list.add(type.cast(e));
		}

		return list;
	}
}
