package org.powerbot.bot.rt4.client;

import org.powerbot.bot.*;

public class Cache extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public Cache(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public HashTable getTable() {
		return new HashTable(reflector, reflector.access(this, a));
	}
}
