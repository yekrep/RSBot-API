package org.powerbot.bot.rt4.client;

import org.powerbot.bot.reflect.ReflectionEngine;

public class ItemNode extends Node {
	public ItemNode(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public int getItemId() {
		return engine.accessInt(this);
	}

	public int getStackSize() {
		return engine.accessInt(this);
	}
}
