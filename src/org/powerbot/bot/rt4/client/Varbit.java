package org.powerbot.bot.rt4.client;

import org.powerbot.bot.Reflector;

public class Varbit extends Node {
	public Varbit(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getIndex() {
		return reflector.accessInt(this);
	}

	public int getStartBit() {
		return reflector.accessInt(this);
	}

	public int getEndBit() {
		return reflector.accessInt(this);
	}
}
