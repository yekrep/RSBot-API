package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Varpbits extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public Varpbits(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int[] get() {
		return reflector.accessInts(this, a);
	}
}
