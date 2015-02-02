package org.powerbot.bot.rt6;

import org.powerbot.bot.Reflector;
import org.powerbot.bot.rt6.client.AnimationBridge;
import org.powerbot.bot.rt6.client.RenderableEntity;

public class DynamicFloorObject extends RenderableEntity {
	public DynamicFloorObject(final Reflector reflector, final Object obj) {
		super(reflector, obj);
	}

	public AnimationBridge getBridge() {
		return new AnimationBridge(reflector, reflector.access(this));
	}
}
