package org.powerbot.bot.rt4.client;

import org.powerbot.bot.reflect.ReflectionEngine;

public class Varbit extends Node {
	public Varbit(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public int getIndex() {
		return engine.accessInt(this);
	}

	public int getStartBit() {
		return engine.accessInt(this);
	}

	public int getEndBit() {
		return engine.accessInt(this);
	}
}
