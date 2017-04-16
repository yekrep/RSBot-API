package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class ObjectConfig extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache(),
			d = new Reflector.FieldCache(),
			e = new Reflector.FieldCache();

	public ObjectConfig(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public String getName() {
		return reflector.accessString(this, a);
	}

	public String[] getActions() {
		return reflector.access(this, b, String[].class);
	}

	public int[] getConfigs() {
		return reflector.accessInts(this, c);
	}

	public int getVarpbitIndex() {
		return reflector.accessInt(this, d);
	}

	public int getVarbit() {
		return reflector.accessInt(this, e);
	}
}
