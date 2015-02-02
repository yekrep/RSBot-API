package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class FloorObject extends RenderableEntity {
	public FloorObject(final Reflector reflector, final Object obj) {
		super(reflector, obj);
	}

	public int getId() {
		return reflector.accessInt(this);
	}
}
