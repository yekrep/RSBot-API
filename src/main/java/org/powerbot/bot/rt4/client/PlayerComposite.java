package org.powerbot.bot.rt4.client;

import org.powerbot.bot.*;

public class PlayerComposite extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public PlayerComposite(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int[] getAppearance() {
		return reflector.accessInts(this, a);
	}
}
