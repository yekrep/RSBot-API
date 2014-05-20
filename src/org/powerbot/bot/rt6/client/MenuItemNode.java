package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class MenuItemNode extends NodeSub {
	public MenuItemNode(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public String getAction() {
		return reflector.accessString(this);
	}

	public String getOption() {
		return reflector.accessString(this);
	}
}
