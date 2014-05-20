package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class ItemPile extends ContextAccessor {
	public ItemPile(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public ItemNode getItem1() {
		return new ItemNode(engine, engine.access(this));
	}

	public ItemNode getItem2() {
		return new ItemNode(engine, engine.access(this));
	}

	public ItemNode getItem3() {
		return new ItemNode(engine, engine.access(this));
	}

	public int getY() {
		return engine.accessInt(this);
	}
}
