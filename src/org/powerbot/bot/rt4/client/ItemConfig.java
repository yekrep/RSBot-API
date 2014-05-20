package org.powerbot.bot.rt4.client;

import org.powerbot.bot.Reflector;

public class ItemConfig extends Node {
	public ItemConfig(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public String getName() {
		return reflector.accessString(this);
	}

	public boolean isMembers() {
		return reflector.accessBool(this);
	}

	public String[] getActions1() {
		return reflector.access(this, String[].class);
	}

	public String[] getActions2() {
		return reflector.access(this, String[].class);
	}
}
