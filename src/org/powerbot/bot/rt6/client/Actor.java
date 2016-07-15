package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class Actor extends RenderableEntity {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache(),
			d = new Reflector.FieldCache(),
			e = new Reflector.FieldCache(),
			f = new Reflector.FieldCache(),
			g = new Reflector.FieldCache(),
			h = new Reflector.FieldCache(),
			i = new Reflector.FieldCache();

	public Actor(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getInteracting() {
		return reflector.accessInt(this, a);
	}

	public int getSpeed() {
		return reflector.accessInt(this, b);
	}

	public int[] getAnimationQueue() {
		return reflector.accessInts(this, c);
	}

	public Animator getAnimation() {
		return new Animator(reflector, reflector.access(this, d));
	}

	public int getHeight() {
		return reflector.accessInt(this, e);
	}

	public LinkedList getCombatStatusList() {
		return new LinkedList(reflector, reflector.access(this, f));
	}

	public int getOrientation() {
		return reflector.accessInt(this, g);
	}

	public OverheadMessage getMessage() {
		return new OverheadMessage(reflector, reflector.access(this, h));
	}

	public Animator getPassiveAnimation() {
		return new Animator(reflector, reflector.access(this, i));
	}
}
