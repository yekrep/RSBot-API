package org.powerbot.bot.rt4.client;

import org.powerbot.bot.Reflector;

public class ItemNode extends Node {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache();

	public ItemNode(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getItemId() {
		return reflector.accessInt(this, a);
	}

	public int getStackSize() {
		return reflector.accessInt(this, b);
	}
}
