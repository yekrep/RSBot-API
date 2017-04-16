package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class RenderableEntity extends Entity {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache(),
			d = new Reflector.FieldCache();

	public RenderableEntity(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public short getX1() {
		return reflector.accessShort(this, a);
	}

	public short getX2() {
		return reflector.accessShort(this, b);
	}

	public short getY1() {
		return reflector.accessShort(this, c);
	}

	public short getY2() {
		return reflector.accessShort(this, d);
	}
}
