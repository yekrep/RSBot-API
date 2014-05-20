package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class RSItem extends ContextAccessor {
	public RSItem(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getId() {
		return engine.accessInt(this);
	}

	public int getStackSize() {
		return engine.accessInt(this);
	}
}
