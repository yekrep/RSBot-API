package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class FloorObject extends WalkableObject {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public FloorObject(final Reflector reflector, final Object obj) {
		super(reflector, obj);
	}

	public int getId() {
		return reflector.accessInt(this, a);
	}
}
