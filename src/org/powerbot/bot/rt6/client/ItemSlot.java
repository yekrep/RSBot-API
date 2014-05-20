package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class ItemSlot extends Node {
	public ItemSlot(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int[] getIds() {
		return reflector.accessInts(this);
	}

	public int[] getStackSizes() {
		return reflector.accessInts(this);
	}
}
