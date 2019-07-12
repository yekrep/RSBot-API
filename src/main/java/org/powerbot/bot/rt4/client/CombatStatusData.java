package org.powerbot.bot.rt4.client;

import org.powerbot.bot.*;

public class CombatStatusData extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache();

	public CombatStatusData(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getHealthRatio() {
		return reflector.accessInt(this, a);
	}

	public int getCycleEnd() {
		return reflector.accessInt(this, b);
	}
}
