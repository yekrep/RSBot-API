package org.powerbot.bot.rt6.client;

import org.powerbot.bot.reflect.ContextAccessor;
import org.powerbot.bot.reflect.ReflectionEngine;

public class Skill extends ContextAccessor {
	public Skill(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public int getLevel() {
		return engine.accessInt(this);
	}

	public int getRealLevel() {
		return engine.accessInt(this);
	}

	public int getExperience() {
		return engine.accessInt(this);
	}
}
