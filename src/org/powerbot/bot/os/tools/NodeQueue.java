package org.powerbot.bot.os.tools;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.bot.os.client.Node;
import org.powerbot.bot.os.client.NodeDeque;

public class NodeQueue {
	public static <E extends Node> List<E> get(final NodeDeque q, final Class<E> type) {
		final List<E> list = new ArrayList<E>();
		Node e;

		if (q == null || (e = q.getSentinel()) == null) {
			return list;
		}

		for (; type.isInstance(e); e = e.getNext()) {
			list.add(type.cast(e));
		}

		return list;
	}
}
