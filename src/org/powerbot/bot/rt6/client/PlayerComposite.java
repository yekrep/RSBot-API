package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class PlayerComposite extends ReflectProxy {
	public PlayerComposite(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getNpcId() {
		return reflector.accessInt(this);
	}

	public int[] getAppearance() {
		return reflector.accessInts(this);
	}
}
