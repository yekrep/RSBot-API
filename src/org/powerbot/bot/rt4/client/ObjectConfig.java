package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class ObjectConfig extends ContextAccessor {
	public ObjectConfig(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public String getName() {
		return engine.accessString(this);
	}

	public String[] getActions() {
		return engine.access(this, String[].class);
	}

	public int[] getConfigs() {
		return engine.accessInts(this);
	}

	public int getVarpbitIndex() {
		return engine.accessInt(this);
	}

	public int getVarbit() {
		return engine.accessInt(this);
	}
}
