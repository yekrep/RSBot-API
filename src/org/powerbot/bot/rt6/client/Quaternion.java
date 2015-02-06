package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Quaternion extends ReflectProxy {
	public Quaternion(final Reflector reflector, final Object obj) {
		super(reflector, obj);
	}

	public float x() {
		return reflector.accessFloat(this);
	}

	public float y() {
		return reflector.accessFloat(this);
	}

	public float z() {
		return reflector.accessFloat(this);
	}

	public float w() {
		return reflector.accessFloat(this);
	}
}
