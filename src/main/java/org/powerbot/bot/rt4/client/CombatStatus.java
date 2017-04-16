package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class CombatStatus extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public CombatStatus(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public LinkedList getList() {
		return new LinkedList(reflector, reflector.access(this, a));
	}
}
