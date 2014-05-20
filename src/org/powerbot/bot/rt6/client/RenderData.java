package org.powerbot.bot.rt6.client;

import org.powerbot.bot.reflect.ContextAccessor;
import org.powerbot.bot.reflect.ReflectionEngine;

public class RenderData extends ContextAccessor {
	public RenderData(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public float[] getFloats() {
		return engine.access(this, float[].class);
	}
}
