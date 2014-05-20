package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class RSInteractable extends ReflectProxy {
	public RSInteractable(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public RSInteractableData getData() {
		return new RSInteractableData(reflector, reflector.access(this));
	}

	public byte getPlane() {
		return reflector.accessByte(this);
	}
}
