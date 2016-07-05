package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Sequence extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public Sequence(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getId() {
		return reflector.accessInt(this, a);
	}
}
