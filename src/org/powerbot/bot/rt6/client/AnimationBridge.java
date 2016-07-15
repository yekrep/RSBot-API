package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class AnimationBridge extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache(),
			d = new Reflector.FieldCache(),
			e = new Reflector.FieldCache();

	public AnimationBridge(final Reflector reflector, final Object obj) {
		super(reflector, obj);
	}

	public int getVariableId() {
		return reflector.accessInt(this, a);
	}

	public int getId() {
		return reflector.accessInt(this, b);
	}

	public int getOrientation() {
		return reflector.accessInt(this, c);
	}

	public int getType() {
		return reflector.accessInt(this, d);
	}

	public Animator getAnimator() {
		return new Animator(reflector, reflector.access(this, e));
	}
}
