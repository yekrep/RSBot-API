package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Skill extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache();

	public Skill(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getEffectiveLevel() {
		return reflector.accessInt(this, a);
	}

	public int getLevel() {
		return reflector.accessInt(this, b);
	}

	public int getExperience() {
		return reflector.accessInt(this, c);
	}
}
