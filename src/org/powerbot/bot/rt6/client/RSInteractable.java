package org.powerbot.bot.rt6.client;

import org.powerbot.bot.reflect.ContextAccessor;
import org.powerbot.bot.reflect.ReflectionEngine;

public class RSInteractable extends ContextAccessor {
	public RSInteractable(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public RSInteractableData getData() {
		return new RSInteractableData(engine, engine.access(this));
	}

	public byte getPlane() {
		return engine.accessByte(this);
	}
}
