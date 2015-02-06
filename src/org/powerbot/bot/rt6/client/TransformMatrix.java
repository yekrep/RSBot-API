package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class TransformMatrix extends ReflectProxy {
	public TransformMatrix(final Reflector reflector, final Object obj) {
		super(reflector, obj);
	}

	public float m00() {
		return reflector.accessFloat(this);
	}

	public float m01() {
		return reflector.accessFloat(this);
	}

	public float m02() {
		return reflector.accessFloat(this);
	}

	public float m03() {
		return reflector.accessFloat(this);
	}

	public float m10() {
		return reflector.accessFloat(this);
	}

	public float m11() {
		return reflector.accessFloat(this);
	}

	public float m12() {
		return reflector.accessFloat(this);
	}

	public float m13() {
		return reflector.accessFloat(this);
	}

	public float m20() {
		return reflector.accessFloat(this);
	}

	public float m21() {
		return reflector.accessFloat(this);
	}

	public float m22() {
		return reflector.accessFloat(this);
	}

	public float m23() {
		return reflector.accessFloat(this);
	}
}
