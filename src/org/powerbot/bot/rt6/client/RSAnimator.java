package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class RSAnimator extends ContextAccessor {
	public RSAnimator(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Sequence getSequence() {
		return new Sequence(engine, engine.access(this));
	}
}
