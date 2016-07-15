package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class ItemNode extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache();

	public ItemNode(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getId() {
		return reflector.accessInt(this, a);
	}

	public int getStackSize() {
		return reflector.accessInt(this, b);
	}
}
