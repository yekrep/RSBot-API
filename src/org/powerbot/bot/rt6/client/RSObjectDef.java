package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class RSObjectDef extends ContextAccessor {
	public RSObjectDef(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public String getName() {
		return engine.access(this, String.class);
	}

	public String[] getActions() {
		return engine.access(this, String[].class);
	}

	public RSObjectDefLoader getLoader() {
		return new RSObjectDefLoader(engine, engine.access(this));
	}

	public int getID() {
		return engine.accessInt(this);
	}

	public int getClippingType() {
		return engine.accessInt(this);
	}
}
