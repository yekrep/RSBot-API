package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class PlayerComposite extends ContextAccessor {
	public PlayerComposite(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int[] getAppearance() {
		return engine.accessInts(this);
	}
}
