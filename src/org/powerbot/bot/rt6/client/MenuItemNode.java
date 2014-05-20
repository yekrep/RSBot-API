package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class MenuItemNode extends NodeSub {
	public MenuItemNode(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public String getAction() {
		return engine.access(this, String.class);
	}

	public String getOption() {
		return engine.access(this, String.class);
	}
}
