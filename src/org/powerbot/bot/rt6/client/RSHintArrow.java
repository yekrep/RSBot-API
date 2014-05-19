package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class RSHintArrow extends ContextAccessor {
	public RSHintArrow(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public int getTargetID() {
		return engine.accessInt(this);
	}

	public int getPlane() {
		return engine.accessInt(this);
	}

	public int getX() {
		return engine.accessInt(this);
	}

	public int getY() {
		return engine.accessInt(this);
	}

	public int getType() {
		return engine.accessInt(this);
	}
}
