package org.powerbot.bot.rt6.client;

import org.powerbot.bot.reflect.ReflectionEngine;

public class RSItemPile extends RSInteractable {
	public RSItemPile(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public int getID_1() {
		return engine.accessInt(this);
	}

	public int getID_2() {
		return engine.accessInt(this);
	}

	public int getID_3() {
		return engine.accessInt(this);
	}

	public int getHeight() {
		return engine.accessInt(this);
	}
}
