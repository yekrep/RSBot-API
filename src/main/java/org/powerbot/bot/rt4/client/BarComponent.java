package org.powerbot.bot.rt4.client;

import org.powerbot.bot.*;

public class BarComponent extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public BarComponent(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getWidth() {
		return reflector.accessInt(this, a);
	}
}
