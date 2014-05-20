package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class RSInteractableData extends ReflectProxy {
	public RSInteractableData(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public RSInteractableLocation getLocation() {
		return new RSInteractableLocation(reflector, reflector.access(this));
	}
}
