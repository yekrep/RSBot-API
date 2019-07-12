package org.powerbot.bot.rt4.client;

import org.powerbot.bot.*;

public class WidgetNode extends Node {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public WidgetNode(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getUid() {
		return reflector.accessInt(this, a);
	}
}
