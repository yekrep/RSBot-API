package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class RSInteractableData extends ContextAccessor {
	public RSInteractableData(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public RSInteractableLocation getLocation() {
		return new RSInteractableLocation(engine, engine.access(this));
	}
}
