package org.powerbot.bot.rt6.client;

import org.powerbot.bot.*;

public class Entity extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public Entity(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public GameLocation getLocation() {
		return new GameLocation(reflector, reflector.access(this, a));
	}
}
