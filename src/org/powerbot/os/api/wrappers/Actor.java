package org.powerbot.os.api.wrappers;

import org.powerbot.os.api.MethodContext;
import org.powerbot.os.api.MethodProvider;

public abstract class Actor extends MethodProvider implements Validatable {
	public Actor(final MethodContext ctx) {
		super(ctx);
	}

	protected abstract org.powerbot.os.client.Actor getActor();

	public abstract String getName();

	public abstract int getCombatLevel();

	@Override
	public int hashCode() {
		final org.powerbot.os.client.Actor actor = getActor();
		return actor != null ? System.identityHashCode(actor) : super.hashCode();
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !Actor.class.isAssignableFrom(o.getClass())) {
			return false;
		}
		final Actor actor = Actor.class.cast(o);
		return actor.getActor() == getActor();
	}
}
