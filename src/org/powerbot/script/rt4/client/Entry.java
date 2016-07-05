package org.powerbot.bot.rt4.client;

import org.powerbot.bot.Reflector;

public class Entry extends Node {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public Entry(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Entry getNext() {
		return new Entry(reflector, reflector.access(this, a));
	}
}
