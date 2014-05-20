package org.powerbot.bot.rt4.client;

import org.powerbot.bot.Reflector;

public class WidgetNode extends Node {
	public WidgetNode(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getUid() {
		return engine.accessInt(this);
	}
}
