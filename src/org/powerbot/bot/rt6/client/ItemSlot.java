package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectionEngine;

public class ItemSlot extends Node {
	public ItemSlot(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public int[] getIds() {
		return engine.access(this, int[].class);
	}

	public int[] getStackSizes() {
		return engine.access(this, int[].class);
	}
}
