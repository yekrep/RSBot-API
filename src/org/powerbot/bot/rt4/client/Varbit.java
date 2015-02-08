package org.powerbot.bot.rt4.client;

import org.powerbot.bot.Reflector;

public class Varbit extends Node {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache();

	public Varbit(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getIndex() {
		return reflector.accessInt(this, a);
	}

	public int getStartBit() {
		return reflector.accessInt(this, b);
	}

	public int getEndBit() {
		return reflector.accessInt(this, c);
	}
}
