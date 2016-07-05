package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class PlayerComposite extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache();

	public PlayerComposite(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getNpcId() {
		return reflector.accessInt(this, a);
	}

	public int[] getAppearance() {
		return reflector.accessInts(this, b);
	}
}
