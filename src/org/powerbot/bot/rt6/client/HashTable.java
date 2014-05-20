package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class HashTable extends ContextAccessor {
	public HashTable(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Node[] getBuckets() {
		final Object[] arr = engine.access(this, Object[].class);
		final Node[] arr2 = arr != null ? new Node[arr.length] : null;
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new Node(engine, arr[i]);
			}
		}
		return arr2;
	}
}
