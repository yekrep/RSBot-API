package org.powerbot.os.api.wrappers;

import org.powerbot.os.api.MethodContext;
import org.powerbot.os.api.MethodProvider;
import org.powerbot.os.client.Client;

public abstract class Actor extends MethodProvider implements Locatable, Validatable {
	public Actor(final MethodContext ctx) {
		super(ctx);
	}

	protected abstract org.powerbot.os.client.Actor getActor();

	public abstract String getName();

	public abstract int getCombatLevel();

	public int getAnimation() {
		final org.powerbot.os.client.Actor actor = getActor();
		return actor != null ? actor.getAnimation() : -1;
	}

	public int getSpeed() {
		final org.powerbot.os.client.Actor actor = getActor();
		return actor != null ? actor.getSpeed() : -1;
	}

	public String getOverheadMessage() {
		final org.powerbot.os.client.Actor actor = getActor();
		final String str = actor != null ? actor.getOverheadMessage() : "";
		return str != null ? str : "";
	}

	public RelativePosition getRelativePosition() {
		final org.powerbot.os.client.Actor actor = getActor();
		final int x, z;
		if (actor != null) {
			x = actor.getX();
			z = actor.getZ();
		} else x = z = 0;
		return new RelativePosition(x, z);
	}

	@Override
	public Tile getLocation() {
		final Client client = ctx.getClient();
		final org.powerbot.os.client.Actor actor = getActor();
		if (client != null && actor != null) {
			return new Tile(client.getOffsetX() + (actor.getX() >> 7), client.getOffsetY() + (actor.getZ() >> 7), client.getFloor());
		} else return new Tile(-1, -1, -1);
	}

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
