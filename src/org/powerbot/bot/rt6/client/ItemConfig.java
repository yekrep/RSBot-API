package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class ItemConfig extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache(),
			d = new Reflector.FieldCache(),
			e = new Reflector.FieldCache();

	public ItemConfig(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public String[] getActions() {
		return reflector.access(this, a, String[].class);
	}

	public int getId() {
		return reflector.accessInt(this, b);
	}

	public String getName() {
		return reflector.accessString(this, c);
	}

	public boolean isMembersObject() {
		return reflector.accessBool(this, d);
	}

	public String[] getGroundActions() {
		return reflector.access(this, e, String[].class);
	}
}
