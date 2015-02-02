package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class BoundaryObject extends RenderableEntity {
	public BoundaryObject(final Reflector reflector, final Object obj) {
		super(reflector, obj);
	}

	public byte getOrientation() {
		return reflector.accessByte(this);
	}

	public int getId() {
		return reflector.accessInt(this);
	}
}
