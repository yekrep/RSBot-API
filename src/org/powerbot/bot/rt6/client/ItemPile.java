package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class ItemPile extends Entity {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache(),
			d = new Reflector.FieldCache();

	public ItemPile(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getId_1() {
		return reflector.accessInt(this, a);
	}

	public int getId_2() {
		return reflector.accessInt(this, b);
	}

	public int getId_3() {
		return reflector.accessInt(this, c);
	}

	public int getHeight() {
		return reflector.accessInt(this, d);
	}
}
