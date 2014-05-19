package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class AbstractModel extends ContextAccessor {
	public AbstractModel(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public int[] getXPoints() {
		return engine.access(this, int[].class);
	}

	public int[] getYPoints() {
		return engine.access(this, int[].class);
	}

	public int[] getZPoints() {
		return engine.access(this, int[].class);
	}

	public short[] getIndices1() {
		return engine.access(this, short[].class);
	}

	public short[] getIndices2() {
		return engine.access(this, short[].class);
	}

	public short[] getIndices3() {
		return engine.access(this, short[].class);
	}
}
