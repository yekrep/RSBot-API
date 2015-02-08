package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class ComponentContainer extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public ComponentContainer(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Widget[] getComponents() {
		final Object[] arr = reflector.access(this, a, Object[].class);
		final Widget[] arr2 = arr != null ? new Widget[arr.length] : new Widget[0];
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new Widget(reflector, arr[i]);
			}
		}
		return arr2;
	}
}
