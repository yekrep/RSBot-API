package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class OverheadMessage extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public OverheadMessage(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public String getText() {
		return reflector.accessString(this, a);
	}
}
