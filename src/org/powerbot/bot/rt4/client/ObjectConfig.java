package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class ObjectConfig extends ContextAccessor {
	public ObjectConfig(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public String getName() {
		return engine.access(this, String.class);
	}

	public String[] getActions() {
		return engine.access(this, String[].class);
	}

	public int[] getConfigs() {
		return engine.access(this, int[].class);
	}

	public int getVarpbitIndex() {
		return engine.accessInt(this);
	}

	public int getVarbit() {
		return engine.accessInt(this);
	}
}
