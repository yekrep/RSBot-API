package org.powerbot.bot.rt4;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.bot.rt4.client.Node;
import org.powerbot.bot.rt4.client.NodeDeque;

public class NodeQueue {
	public static <E extends Node> List<E> get(final NodeDeque q, final Class<E> type) {
		final List<E> list = new ArrayList<E>();
		Node e;

		if (q == null || (e = q.getSentinel()) == null) {
			return list;
		}
		e = e.getNext();

		for (; e != null && type.isInstance(e); e = e.getNext()) {
			list.add(type.cast(e));
		}

		return list;
	}
}
