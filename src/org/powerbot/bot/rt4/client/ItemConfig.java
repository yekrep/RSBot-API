package org.powerbot.bot.rt4.client;

import org.powerbot.bot.Reflector;

public class ItemConfig extends Node {
	public ItemConfig(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public String getName() {
		return engine.accessString(this);
	}

	public boolean isMembers() {
		return engine.accessBool(this);
	}

	public String[] getActions1() {
		return engine.access(this, String[].class);
	}

	public String[] getActions2() {
		return engine.access(this, String[].class);
	}
}
