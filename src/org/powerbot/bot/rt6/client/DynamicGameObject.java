package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;
import org.powerbot.bot.rt6.client.AnimationBridge;
import org.powerbot.bot.rt6.client.RenderableEntity;

public class DynamicGameObject extends RenderableEntity {
	public DynamicGameObject(final Reflector reflector, final Object obj) {
		super(reflector, obj);
	}

	public AnimationBridge getBridge() {
		return new AnimationBridge(reflector, reflector.access(this));
	}
}
