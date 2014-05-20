package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class RenderData extends ContextAccessor {
	public RenderData(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public float[] getFloats() {
		return engine.access(this, float[].class);
	}
}
