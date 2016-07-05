package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class RelativePosition extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache();
	public RelativePosition(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public float getX() {
		return reflector.accessFloat(this, a);
	}

	public float getY() {
		return reflector.accessFloat(this, b);
	}

	public float getZ() {
		return reflector.accessFloat(this, c);
	}
}
