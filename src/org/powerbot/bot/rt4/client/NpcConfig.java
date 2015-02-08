package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class NpcConfig extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache(),
			d = new Reflector.FieldCache(),
			e = new Reflector.FieldCache(),
			f = new Reflector.FieldCache(),
			g = new Reflector.FieldCache();

	public NpcConfig(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getId() {
		return reflector.accessInt(this, a);
	}

	public int getLevel() {
		return reflector.accessInt(this, b);
	}

	public String getName() {
		return reflector.accessString(this, c);
	}

	public String[] getActions() {
		return reflector.access(this, d, String[].class);
	}

	public int[] getConfigs() {
		return reflector.accessInts(this, e);
	}

	public int getVarpbitIndex() {
		return reflector.accessInt(this, f);
	}

	public int getVarbit() {
		return reflector.accessInt(this, g);
	}
}
