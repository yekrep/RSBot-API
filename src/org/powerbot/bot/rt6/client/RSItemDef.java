package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class RSItemDef extends ReflectProxy {
	public RSItemDef(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public String[] getActions() {
		return reflector.access(this, String[].class);
	}

	public int getID() {
		return reflector.accessInt(this);
	}

	public String getName() {
		return reflector.accessString(this);
	}

	public boolean isMembersObject() {
		return reflector.accessBool(this);
	}

	public String[] getGroundActions() {
		return reflector.access(this, String[].class);
	}
}
