package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class ItemResources extends Resources {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public ItemResources(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Cache getModelCache() {
		return new Cache(reflector, reflector.access(this, a));
	}
}
