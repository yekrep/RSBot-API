package org.powerbot.bot.rt4.client;

import org.powerbot.bot.Reflector;

public class ItemNode extends Node {
	public ItemNode(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getItemId() {
		return engine.accessInt(this);
	}

	public int getStackSize() {
		return engine.accessInt(this);
	}
}
