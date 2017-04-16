package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class ItemPile extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache(),
			d = new Reflector.FieldCache();

	public ItemPile(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public ItemNode getItem1() {
		return new ItemNode(reflector, reflector.access(this, a));
	}

	public ItemNode getItem2() {
		return new ItemNode(reflector, reflector.access(this, b));
	}

	public ItemNode getItem3() {
		return new ItemNode(reflector, reflector.access(this, c));
	}

	public int getY() {
		return reflector.accessInt(this, d);
	}
}
