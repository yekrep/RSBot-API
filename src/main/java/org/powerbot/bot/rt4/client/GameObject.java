package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class GameObject extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache(),
			d = new Reflector.FieldCache(),
			e = new Reflector.FieldCache(),
			f = new Reflector.FieldCache(),
			g = new Reflector.FieldCache(),
			h = new Reflector.FieldCache();

	public GameObject(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public long getUid() {
		return reflector.accessLong(this, a);
	}

	public int getMeta() {
		return reflector.accessInt(this, b);
	}

	public int getX() {
		return reflector.accessInt(this, c);
	}

	public int getZ() {
		return reflector.accessInt(this, d);
	}

	public int getX1() {
		return reflector.accessInt(this, e);
	}

	public int getY1() {
		return reflector.accessInt(this, f);
	}

	public int getX2() {
		return reflector.accessInt(this, g);
	}

	public int getY2() {
		return reflector.accessInt(this, h);
	}
}
