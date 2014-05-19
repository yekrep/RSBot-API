package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class HashTable extends ContextAccessor {
	public HashTable(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public Node[] getBuckets() {
		return engine.access(this, Node[].class);
	}
}
