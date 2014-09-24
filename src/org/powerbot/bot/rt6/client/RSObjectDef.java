package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class RSObjectDef extends ReflectProxy {
	public RSObjectDef(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public String getName() {
		return reflector.accessString(this);
	}

	public String[] getActions() {
		return reflector.access(this, String[].class);
	}

	public int getID() {
		return reflector.accessInt(this);
	}

	public int getClippingType() {
		return reflector.accessInt(this);
	}
}
