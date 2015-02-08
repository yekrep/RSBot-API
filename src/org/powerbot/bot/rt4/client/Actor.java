package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Actor extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache(),
			d = new Reflector.FieldCache(),
			e = new Reflector.FieldCache(),
			f = new Reflector.FieldCache(),
			g = new Reflector.FieldCache(),
			h = new Reflector.FieldCache(),
			i = new Reflector.FieldCache(),
			j = new Reflector.FieldCache(),
			k = new Reflector.FieldCache();

	public Actor(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getX() {
		return reflector.accessInt(this, a);
	}

	public int getZ() {
		return reflector.accessInt(this, b);
	}

	public int getHeight() {
		return reflector.accessInt(this, c);
	}

	public int getAnimation() {
		return reflector.accessInt(this, d);
	}

	public int getSpeed() {
		return reflector.accessInt(this, e);
	}

	public int getCurrentHealth() {
		return reflector.accessInt(this, f);
	}

	public int getMaxHealth() {
		return reflector.accessInt(this, g);
	}

	public int getCycleEnd() {
		return reflector.accessInt(this, h);
	}

	public String getOverheadMessage() {
		return reflector.accessString(this, i);
	}

	public int getOrientation() {
		return reflector.accessInt(this, j);
	}

	public int getInteractingIndex() {
		return reflector.accessInt(this, k);
	}
}
