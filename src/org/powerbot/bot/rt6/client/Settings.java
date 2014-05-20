package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Settings extends ReflectProxy {
	public Settings(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int[] getData() {
		return reflector.accessInts(this);
	}
}
