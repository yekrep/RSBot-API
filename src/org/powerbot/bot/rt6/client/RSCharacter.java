package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class RSCharacter extends RSAnimable {
	public RSCharacter(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getInteracting() {
		return reflector.accessInt(this);
	}

	public int isMoving() {
		return reflector.accessInt(this);
	}

	public int[] getAnimationQueue() {
		return reflector.accessInts(this);
	}

	public RSAnimator getAnimation() {
		return new RSAnimator(reflector, reflector.access(this));
	}

	public int getHeight() {
		return reflector.accessInt(this);
	}

	public LinkedList getCombatStatusList() {
		return new LinkedList(reflector, reflector.access(this));
	}

	public int getOrientation() {
		return reflector.accessInt(this);
	}

	public RSMessageData getMessageData() {
		return new RSMessageData(reflector, reflector.access(this));
	}

	public RSAnimator getPassiveAnimation() {
		return new RSAnimator(reflector, reflector.access(this));
	}
}
