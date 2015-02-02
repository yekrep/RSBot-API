package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class Actor extends RenderableEntity {
	public Actor(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getInteracting() {
		return reflector.accessInt(this);
	}

	public int getSpeed() {
		return reflector.accessInt(this);
	}

	public int[] getAnimationQueue() {
		return reflector.accessInts(this);
	}

	public Animator getAnimation() {
		return new Animator(reflector, reflector.access(this));
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

	public OverheadMessage getMessage() {
		return new OverheadMessage(reflector, reflector.access(this));
	}

	public Animator getPassiveAnimation() {
		return new Animator(reflector, reflector.access(this));
	}
}
