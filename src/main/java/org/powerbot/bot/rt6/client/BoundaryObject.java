package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class BoundaryObject extends RenderableEntity {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache();

	public BoundaryObject(final Reflector reflector, final Object obj) {
		super(reflector, obj);
	}

	public byte getOrientation() {
		return reflector.accessByte(this, a);
	}

	public int getId() {
		return reflector.accessInt(this, b);
	}

	public int getType() {
		return reflector.accessByte(this, c);
	}
}
