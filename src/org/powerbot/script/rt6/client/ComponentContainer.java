package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class ComponentContainer extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public ComponentContainer(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Object[] getComponents() {
		final Object[] arr = reflector.access(this, a, Object[].class);
		return arr != null ? arr : new Widget[0];
	}
}
