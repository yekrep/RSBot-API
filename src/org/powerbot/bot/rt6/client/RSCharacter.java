package org.powerbot.bot.rt6.client;

import org.powerbot.bot.reflect.ReflectionEngine;

public class RSCharacter extends RSAnimable {
	public RSCharacter(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public int getInteracting() {
		return engine.accessInt(this);
	}

	public int isMoving() {
		return engine.accessInt(this);
	}

	public int[] getAnimationQueue() {
		return engine.access(this, int[].class);
	}

	public RSAnimator getAnimation() {
		return new RSAnimator(engine, engine.access(this));
	}

	public int getHeight() {
		return engine.accessInt(this);
	}

	public LinkedList getCombatStatusList() {
		return new LinkedList(engine, engine.access(this));
	}

	public int getOrientation() {
		return engine.accessInt(this);
	}

	public RSMessageData getMessageData() {
		return new RSMessageData(engine, engine.access(this));
	}

	public RSAnimator getPassiveAnimation() {
		return new RSAnimator(engine, engine.access(this));
	}
}
