package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Animator extends ReflectProxy {
	public Animator(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Sequence getSequence() {
		return new Sequence(reflector, reflector.access(this));
	}
}
