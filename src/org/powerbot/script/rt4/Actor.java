package org.powerbot.script.rt4;

import java.awt.Point;

import org.powerbot.bot.rt4.client.Client;
import org.powerbot.script.InteractiveEntity;
import org.powerbot.script.Nameable;
import org.powerbot.script.Tile;
import org.powerbot.script.Validatable;

/**
 * Actor
 * A base class of all characters within Runescape.
 */
public abstract class Actor extends Interactive implements InteractiveEntity, Nameable, Validatable {
	Actor(final ClientContext ctx) {
		super(ctx);
		bounds(new int[]{-32, 32, -192, 0, -32, 32});
	}

	@Override
	public void bounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) {
		boundingModel.set(new BoundingModel(ctx, x1, x2, y1, y2, z1, z2) {
			@Override
			public int x() {
				final int r = relative();
				return r >> 16;
			}

			@Override
			public int z() {
				final int r = relative();
				return r & 0xffff;
			}
		});
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

	public int orientation() {
		final org.powerbot.bot.rt4.client.Actor actor = getActor();
		return actor != null ? actor.getOrientation() / 256 : -1;
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
		return false;//TODO: this
	}
	}

	public int health() {
		final org.powerbot.bot.rt4.client.Actor actor = getActor();
		return -1;//TODO: this
	}

	public int maxHealth() {
		final org.powerbot.bot.rt4.client.Actor actor = getActor();
		return -1;//TODO: this
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
			if (pos == client.getPlayerIndex()) {
				return new Player(ctx, client.getPlayer());
			}
			final org.powerbot.bot.rt4.client.Player[] players = client.getPlayers();
			return pos >= 0 && pos < players.length ? new Player(ctx, players[pos]) : nil;
		}
	}


	public int relative() {
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
		final BoundingModel model2 = boundingModel.get();
		if (actor != null && model2 != null) {
			return model2.nextPoint();
		}
		return new Point(-1, -1);
	}

	@Override
	public Point centerPoint() {
		final org.powerbot.bot.rt4.client.Actor actor = getActor();
		final BoundingModel model2 = boundingModel.get();
		if (actor != null && model2 != null) {
			return model2.centerPoint();
		}
		return new Point(-1, -1);
	}

	@Override
	public boolean contains(final Point point) {
		final org.powerbot.bot.rt4.client.Actor actor = getActor();
		final BoundingModel model2 = boundingModel.get();
		return actor != null && model2 != null && model2.contains(point);
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !Actor.class.isAssignableFrom(o.getClass())) {
			return false;
		}
		final org.powerbot.bot.rt4.client.Actor actor = Actor.class.cast(o).getActor();
		return actor != null && actor.equals(getActor());
	}

	@Override
	public int hashCode() {
		final org.powerbot.bot.rt4.client.Actor actor = getActor();
		return actor != null ? actor.hashCode() : 0;
	}
}
