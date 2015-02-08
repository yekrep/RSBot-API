package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class DynamicGameObject extends RenderableEntity {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public DynamicGameObject(final Reflector reflector, final Object obj) {
		super(reflector, obj);
	}

	public AnimationBridge getBridge() {
		return new AnimationBridge(reflector, reflector.access(this, a));
	}
}
