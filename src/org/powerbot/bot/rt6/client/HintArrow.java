package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class HintArrow extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache(),
			d = new Reflector.FieldCache(),
			e = new Reflector.FieldCache();

	public HintArrow(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getTargetId() {
		return reflector.accessInt(this, a);
	}

	public int getFloor() {
		return reflector.accessInt(this, b);
	}

	public int getX() {
		return reflector.accessInt(this, c);
	}

	public int getY() {
		return reflector.accessInt(this, d);
	}

	public int getType() {
		return reflector.accessInt(this, e);
	}
}
