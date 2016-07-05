package org.powerbot.bot.rt4.client;

import org.powerbot.bot.Reflector;

public class ItemConfig extends Node {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache(),
			d = new Reflector.FieldCache();

	public ItemConfig(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public String getName() {
		return reflector.accessString(this, a);
	}

	public boolean isMembers() {
		return reflector.accessBool(this, b);
	}

	public String[] getActions1() {
		return reflector.access(this, c, String[].class);
	}

	public String[] getActions2() {
		return reflector.access(this, d, String[].class);
	}
}
