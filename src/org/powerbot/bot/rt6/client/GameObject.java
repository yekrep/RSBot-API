package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class GameObject extends RenderableEntity {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache();

	public GameObject(final Reflector reflector, final Object obj) {
		super(reflector, obj);
	}

	public int getId() {
		return reflector.accessInt(this, a);
	}

	public int getType() {
		return reflector.accessByte(this, b);
	}
}
