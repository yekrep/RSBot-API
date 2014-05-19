package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class HashTable extends ContextAccessor {
	public HashTable(final ReflectionEngine engine, final Object parent) {
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

	public int getSize() {
		return engine.accessInt(this);
	}
}
