package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class RSAnimator extends ContextAccessor {
	public RSAnimator(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public Sequence getSequence() {
		return new Sequence(engine, engine.access(this));
	}
}
