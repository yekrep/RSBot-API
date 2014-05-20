package org.powerbot.bot.rt6.client;

import org.powerbot.bot.reflect.ContextAccessor;
import org.powerbot.bot.reflect.ReflectionEngine;

public class RSPlayerComposite extends ContextAccessor {
	public RSPlayerComposite(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public int getNPCID() {
		return engine.accessInt(this);
	}

	public int[] getEquipment() {
		return engine.access(this, int[].class);
	}
}
