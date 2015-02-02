package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class WallObject extends RenderableEntity {
	public WallObject(final Reflector reflector, final Object obj) {
		super(reflector, obj);
	}

	public int getId() {
		return reflector.accessInt(this);
	}
}
