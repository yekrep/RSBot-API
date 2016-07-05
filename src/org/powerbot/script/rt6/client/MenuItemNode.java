package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class MenuItemNode extends NodeSub {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache();

	public MenuItemNode(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public String getAction() {
		return reflector.accessString(this, a);
	}

	public String getOption() {
		return reflector.accessString(this, b);
	}
}
