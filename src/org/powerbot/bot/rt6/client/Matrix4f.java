package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Matrix4f extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public Matrix4f(final Reflector reflector, final Object obj) {
		super(reflector, obj);
	}

	public float[] getMatrix() {
		return reflector.access(this, a, float[].class);
	}
}
