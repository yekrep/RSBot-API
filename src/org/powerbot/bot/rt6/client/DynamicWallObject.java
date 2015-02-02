package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;
import org.powerbot.bot.rt6.client.AnimationBridge;
import org.powerbot.bot.rt6.client.RenderableEntity;

public class DynamicWallObject extends RenderableEntity {
	public DynamicWallObject(final Reflector reflector, final Object obj) {
		super(reflector, obj);
	}

	public AnimationBridge getBridge() {
		return new AnimationBridge(reflector, reflector.access(this));
	}
}
