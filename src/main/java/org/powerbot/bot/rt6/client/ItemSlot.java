package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class ItemSlot extends Node {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache();

	public ItemSlot(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int[] getIds() {
		return reflector.accessInts(this, a);
	}

	public int[] getStackSizes() {
		return reflector.accessInts(this, b);
	}
}
