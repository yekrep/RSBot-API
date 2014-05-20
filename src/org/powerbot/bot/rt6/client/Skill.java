package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Skill extends ReflectProxy {
	public Skill(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getLevel() {
		return reflector.accessInt(this);
	}

	public int getRealLevel() {
		return reflector.accessInt(this);
	}

	public int getExperience() {
		return reflector.accessInt(this);
	}
}
