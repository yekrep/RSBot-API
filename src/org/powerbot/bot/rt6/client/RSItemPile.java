package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class RSItemPile extends RSInteractable {
	public RSItemPile(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getID_1() {
		return reflector.accessInt(this);
	}

	public int getID_2() {
		return reflector.accessInt(this);
	}

	public int getID_3() {
		return reflector.accessInt(this);
	}

	public int getHeight() {
		return reflector.accessInt(this);
	}
}
