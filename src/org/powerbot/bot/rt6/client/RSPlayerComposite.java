package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class RSPlayerComposite extends ReflectProxy {
	public RSPlayerComposite(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getNPCID() {
		return reflector.accessInt(this);
	}

	public int[] getEquipment() {
		return reflector.accessInts(this);
	}
}
