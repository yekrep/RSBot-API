package org.powerbot.bot.rt6.client;

import org.powerbot.bot.*;

public class DynamicFloorObject extends RenderableEntity {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public DynamicFloorObject(final Reflector reflector, final Object obj) {
		super(reflector, obj);
	}

	public AnimationBridge getBridge() {
		return new AnimationBridge(reflector, reflector.access(this, a));
	}
}
