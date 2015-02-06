package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class WalkableObject extends RenderableEntity {
	public WalkableObject(final Reflector reflector, final Object obj) {
		super(reflector, obj);
	}

	public short getHeight() {
		return reflector.accessShort(this);
	}
}
