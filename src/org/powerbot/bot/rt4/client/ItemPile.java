package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class ItemPile extends ReflectProxy {
	public ItemPile(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public ItemNode getItem1() {
		return new ItemNode(reflector, reflector.access(this));
	}

	public ItemNode getItem2() {
		return new ItemNode(reflector, reflector.access(this));
	}

	public ItemNode getItem3() {
		return new ItemNode(reflector, reflector.access(this));
	}

	public int getY() {
		return reflector.accessInt(this);
	}
}
