package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class AnimationBridge extends ReflectProxy {
	public AnimationBridge(final Reflector reflector, final Object obj) {
		super(reflector, obj);
	}

	public int getVariableId() {
		return reflector.accessInt(this);
	}

	public int getId() {
		return reflector.accessInt(this);
	}

	public int getOrientation() {
		return reflector.accessInt(this);
	}

	public int getType() {
		return reflector.accessInt(this);
	}
}
