package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Projectile extends ReflectProxy {
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
			k = new Reflector.FieldCache(),
			l = new Reflector.FieldCache(),
			m = new Reflector.FieldCache(),
			n = new Reflector.FieldCache();

	public int getId() {
		return reflector.accessInt(this, i);
	}

	public Projectile(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getTargetIndex() {
		return reflector.accessInt(this, b);
	}

	public int getStartX() {
		return reflector.accessInt(this, c);
	}

	public int getStartY() {
		return reflector.accessInt(this, d);
	}

	public int getPlane() {
		return reflector.accessInt(this, e);
	}

	public boolean isStarted() {
		return reflector.accessBool(this, f);
	}

	public int getSlope() {
		return reflector.accessInt(this, g);
	}

	public int getEndHeight() {
		return reflector.accessInt(this, h);
	}

	public int getOrientation() {
		return reflector.accessInt(this, j);
	}

	public int getStartDistance() {
		return reflector.accessInt(this, k);
	}

	public int getCycleStart() {
		return reflector.accessInt(this, l);
	}

	public double getX() {
		return reflector.accessDouble(this, m);
	}

	public double getY() {
		return reflector.accessDouble(this, n);
	}


}
