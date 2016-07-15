package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class TransformMatrix extends ReflectProxy {
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
			l = new Reflector.FieldCache();

	public TransformMatrix(final Reflector reflector, final Object obj) {
		super(reflector, obj);
	}

	public float m00() {
		return reflector.accessFloat(this, a);
	}

	public float m01() {
		return reflector.accessFloat(this, b);
	}

	public float m02() {
		return reflector.accessFloat(this, c);
	}

	public float m03() {
		return reflector.accessFloat(this, d);
	}

	public float m10() {
		return reflector.accessFloat(this, e);
	}

	public float m11() {
		return reflector.accessFloat(this, f);
	}

	public float m12() {
		return reflector.accessFloat(this, g);
	}

	public float m13() {
		return reflector.accessFloat(this, h);
	}

	public float m20() {
		return reflector.accessFloat(this, i);
	}

	public float m21() {
		return reflector.accessFloat(this, j);
	}

	public float m22() {
		return reflector.accessFloat(this, k);
	}

	public float m23() {
		return reflector.accessFloat(this, l);
	}
}
