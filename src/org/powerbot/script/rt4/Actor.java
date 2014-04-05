package org.powerbot.script.rt4;

import java.awt.Point;

import org.powerbot.bot.rt4.client.Client;
import org.powerbot.script.Locatable;
import org.powerbot.script.Nameable;
import org.powerbot.script.Tile;
import org.powerbot.script.Validatable;

public abstract class Actor extends Interactive implements Locatable, Nameable, Validatable {
	Actor(final ClientContext ctx) {
		super(ctx);
	}

	protected abstract org.powerbot.bot.rt4.client.Actor getActor();

	public abstract String name();

	public abstract int combatLevel();

	public int animation() {
		final org.powerbot.bot.rt4.client.Actor actor = getActor();
		return actor != null ? actor.getAnimation() : -1;
	}

	public int speed() {
		final org.powerbot.bot.rt4.client.Actor actor = getActor();
		return actor != null ? actor.getSpeed() : -1;
	}

	public String overheadMessage() {
		final org.powerbot.bot.rt4.client.Actor actor = getActor();
		final String str = actor != null ? actor.getOverheadMessage() : "";
		return str != null ? str : "";
	}

	public boolean inMotion() {
		return speed() > 0;
	}

	public boolean inCombat() {
		final Client client = ctx.client();
		final org.powerbot.bot.rt4.client.Actor actor = getActor();
		return !(client == null || actor == null) && actor.getCycleEnd() > client.getCycle();
	}

	public int health() {
		final org.powerbot.bot.rt4.client.Actor actor = getActor();
		return actor != null && inCombat() ? actor.getHealthRatio() : -1;
	}

	public Actor interacting() {
		final Actor nil = ctx.npcs.nil();
		final org.powerbot.bot.rt4.client.Actor actor = getActor();
		final int index = actor != null ? actor.getInteractingIndex() : -1;
		if (index == -1) {
			return nil;
		}
		final Client client = ctx.client();
		if (client == null) {
			return nil;
		}
		if (index < 32768) {
			final org.powerbot.bot.rt4.client.Npc[] npcs = client.getNpcs();
			return index >= 0 && index < npcs.length ? new Npc(ctx, npcs[index]) : nil;
		} else {
			final int pos = index - 32768;
			final org.powerbot.bot.rt4.client.Player[] players = client.getPlayers();
			return pos >= 0 && pos < players.length ? new Player(ctx, players[pos]) : nil;
		}
	}


	public int relativePosition() {
		final org.powerbot.bot.rt4.client.Actor actor = getActor();
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
	public Tile tile() {
		final Client client = ctx.client();
		final org.powerbot.bot.rt4.client.Actor actor = getActor();
		if (client != null && actor != null) {
			return new Tile(client.getOffsetX() + (actor.getX() >> 7), client.getOffsetY() + (actor.getZ() >> 7), client.getFloor());
		}
		return Tile.NIL;
	}

	@Override
	public Point nextPoint() {
		final org.powerbot.bot.rt4.client.Actor actor = getActor();
		if (actor == null) {
			return new Point(-1, -1);
		}
		final ActorCuboid cuboid = new ActorCuboid(ctx, actor);
		return cuboid.nextPoint();
	}

	@Override
	public Point centerPoint() {
		final org.powerbot.bot.rt4.client.Actor actor = getActor();
		if (actor == null) {
			return new Point(-1, -1);
		}
		final ActorCuboid cuboid = new ActorCuboid(ctx, actor);
		return cuboid.centerPoint();
	}

	@Override
	public boolean contains(final Point point) {
		final org.powerbot.bot.rt4.client.Actor actor = getActor();
		if (actor == null) {
			return false;
		}
		final ActorCuboid cuboid = new ActorCuboid(ctx, actor);
		return cuboid.contains(point);
	}

	@Override
	public int hashCode() {
		final org.powerbot.bot.rt4.client.Actor actor = getActor();
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
