package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class RSPlayerComposite extends ContextAccessor {
	public RSPlayerComposite(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getNPCID() {
		return engine.accessInt(this);
	}

	public int[] getEquipment() {
		return engine.access(this, int[].class);
	}
}
