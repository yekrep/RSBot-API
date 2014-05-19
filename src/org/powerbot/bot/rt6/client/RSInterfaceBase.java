package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class RSInterfaceBase extends ContextAccessor {
	public RSInterfaceBase(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public RSInterface[] getComponents() {
		return engine.access(this, RSInterface[].class);
	}
}
