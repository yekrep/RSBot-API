package org.powerbot.api.os.tools;

import java.awt.Point;

import org.powerbot.api.ClientContext;
import org.powerbot.bot.client.Client;

public abstract class Actor extends Interactive implements Locatable, Validatable {
	Actor(final ClientContext ctx) {
		super(ctx);
	}

	protected abstract org.powerbot.bot.client.Actor getActor();

	public abstract String getName();

	public abstract int getCombatLevel();

	public int getAnimation() {
		final org.powerbot.bot.client.Actor actor = getActor();
		return actor != null ? actor.getAnimation() : -1;
	}

	public int getSpeed() {
		final org.powerbot.bot.client.Actor actor = getActor();
		return actor != null ? actor.getSpeed() : -1;
	}

	public String getOverheadMessage() {
		final org.powerbot.bot.client.Actor actor = getActor();
		final String str = actor != null ? actor.getOverheadMessage() : "";
		return str != null ? str : "";
	}

	public boolean isInMotion() {
		return getSpeed() > 0;
	}

	public int getRelativePosition() {
		final org.powerbot.bot.client.Actor actor = getActor();
		final int x, z;
		if (actor != null) {
			x = actor.getX();
			z = actor.getZ();
		} else {
			x = z = 0;
		}
		return (x << 16) | z;
	}

	@Override
	public Tile getLocation() {
		final Client client = ctx.client();
		final org.powerbot.bot.client.Actor actor = getActor();
		if (client != null && actor != null) {
			return new Tile(client.getOffsetX() + (actor.getX() >> 7), client.getOffsetY() + (actor.getZ() >> 7), client.getFloor());
		}
		return new Tile(-1, -1, -1);
	}

	@Override
	public Point getNextPoint() {
		final org.powerbot.bot.client.Actor actor = getActor();
		if (actor == null) {
			return new Point(-1, -1);
		}
		final ActorCuboid cuboid = new ActorCuboid(ctx, actor);
		final Point p = cuboid.getNextPoint();
		if (p.x != -1 && p.y != -1) {
			return p;
		}
		return getScreenPoint();
	}

	@Override
	public Point getCenterPoint() {
		final org.powerbot.bot.client.Actor actor = getActor();
		if (actor == null) {
			return new Point(-1, -1);
		}
		final ActorCuboid cuboid = new ActorCuboid(ctx, actor);
		final Point p = cuboid.getCenterPoint();
		if (p.x != -1 && p.y != -1) {
			return p;
		}
		return getScreenPoint();
	}

	@Override
	public boolean contains(final Point point) {
		final org.powerbot.bot.client.Actor actor = getActor();
		if (actor == null) {
			return false;
		}
		final ActorCuboid cuboid = new ActorCuboid(ctx, actor);
		return cuboid.contains(point);
	}

	private Point getScreenPoint() {
		final org.powerbot.bot.client.Actor actor = getActor();
		if (actor == null) {
			return new Point(-1, -1);
		}
		return ctx.game.worldToScreen(actor.getX(), actor.getZ(), actor.getHeight() / 2);
	}

	@Override
	public int hashCode() {
		final org.powerbot.bot.client.Actor actor = getActor();
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
