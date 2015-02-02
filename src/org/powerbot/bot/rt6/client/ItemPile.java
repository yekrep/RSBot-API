package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class ItemPile extends Entity {
	public ItemPile(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getId_1() {
		return reflector.accessInt(this);
	}

	public int getId_2() {
		return reflector.accessInt(this);
	}

	public int getId_3() {
		return reflector.accessInt(this);
	}

	public int getHeight() {
		return reflector.accessInt(this);
	}
}
