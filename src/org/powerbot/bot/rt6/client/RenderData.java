package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class RenderData extends ReflectProxy {
	public RenderData(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public float[] getFloats() {
		return reflector.access(this, float[].class);
	}
}
