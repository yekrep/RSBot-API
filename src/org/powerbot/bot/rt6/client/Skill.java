package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class Skill extends ContextAccessor {
	public Skill(final Reflector engine, final Object parent) {
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
