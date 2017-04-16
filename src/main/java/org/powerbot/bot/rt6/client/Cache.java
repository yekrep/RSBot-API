package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Cache extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public Cache(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public HashTable getTable() {
		return new HashTable(reflector, reflector.access(this, a));
	}
}
