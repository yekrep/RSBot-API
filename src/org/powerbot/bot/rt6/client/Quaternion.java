package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Quaternion extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache(),
			d = new Reflector.FieldCache();

	public Quaternion(final Reflector reflector, final Object obj) {
		super(reflector, obj);
	}

	public float x() {
		return reflector.accessFloat(this, a);
	}

	public float y() {
		return reflector.accessFloat(this, b);
	}

	public float z() {
		return reflector.accessFloat(this, c);
	}

	public float w() {
		return reflector.accessFloat(this, d);
	}
}
