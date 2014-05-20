package org.powerbot.bot.rt6.client;

import org.powerbot.bot.reflect.ContextAccessor;
import org.powerbot.bot.reflect.ReflectionEngine;

public class RSInteractableData extends ContextAccessor {
	public RSInteractableData(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public RSInteractableLocation getLocation() {
		return new RSInteractableLocation(engine, engine.access(this));
	}
}
