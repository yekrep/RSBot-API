package org.powerbot.script.rt4;

import java.awt.Point;

import org.powerbot.bot.rt4.client.*;
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

	/**
	 * The name of the entity.
	 *
	 * @return The name.
	 */
	public abstract String name();

	/**
	 * The combat level of the entity.
	 *
	 * @return The combat level.
	 */
	public abstract int combatLevel();

	/**
	 * The current animation being enacted by the entity, or {@code -1}
	 * if no animation is occurring.
	 *
	 * @return The animation ID.
	 */
	public int animation() {
		final org.powerbot.bot.rt4.client.Actor actor = getActor();
		return actor != null ? actor.getAnimation() : -1;
	}

	/**
	 * The speed of the entity.
	 *
	 * @return The current speed.
	 */
	public int speed() {
		final org.powerbot.bot.rt4.client.Actor actor = getActor();
		return actor != null ? actor.getSpeed() : -1;
	}

	/**
	 * The way the entity is facing. 0 for North, 1 for East, 2 for South, 3 for West.
	 *
	 * @return The orientation.
	 */
	public int orientation() {
		final org.powerbot.bot.rt4.client.Actor actor = getActor();
		return actor != null ? actor.getOrientation() / 256 : -1;
	}

	/**
	 * The current message which appears above the head of the entity.
	 *
	 * @return The message.
	 */
	public String overheadMessage() {
		final org.powerbot.bot.rt4.client.Actor actor = getActor();
		final String str = actor != null ? actor.getOverheadMessage() : "";
		return str != null ? str : "";
	}

	/**
	 * Whether or not the entity is currently in motion.
	 *
	 * @return {@code true} if in motion, {@code false} otherwise.
	 */
	public boolean inMotion() {
		return speed() > 0;
	}

	/**
	 * Whether or not the entity has a health bar displayed over their head. This is
	 * used to determine whether or not the entity is currently in combat.
	 *
	 * @return {@code true} if the health bar is visible, {@code false} otherwise.
	 */
	@Deprecated
	public boolean inCombat() {
		return healthBarVisible();
	}
	
	/**
	 * Whether or not the entity has a health bar displayed over their head. This can be
	 * used to determine whether or not the entity is currently in combat.
	 *
	 * @return {@code true} if the health bar is visible, {@code false} otherwise.
	 */
	public boolean healthBarVisible() {
		final Client client = ctx.client();
		if (client == null) {
			return false;
		}
		final CombatStatusData[] data = getBarData();
		return data != null && data[1] != null && data[1].getCycleEnd() < client.getCycle();
	}

	/**
	 * The current percent of the health bar.
	 *
	 * @return The percentage of the health bar (0-100).
	 */
	public int healthPercent() {
		if (!valid()) {
			return -1;
		}
		final CombatStatusData[] data = getBarData();
		if (data == null || data[1] == null) {
			return 100;
		}
		return (int) Math.ceil(data[1].getHealthRatio() * 100d / getBarComponent().getWidth());
	}

	/**
	 * The health of the entity.
	 *
	 * @deprecated This was deprecated as the client no longer receives
	 * the absolute health value of the entity. This will now return
	 * {@link Actor#healthPercent()} instead.
	 * @return The health of the entity.
	 */
	@Deprecated
	public int health() {
		return healthPercent();
	}

	/**
	 * The maximum health of the entity.
	 *
	 * @deprecated This was deprecated as the client no longer
	 * receives the absolute health value of the entity. This will
	 * now return {@code 100} instead, rendering it useless. This
	 * function is only kept for backwards compatibility, and should
	 * not be used.
	 * @return {@code 100}
	 */
	@Deprecated
	public int maxHealth() {
		return 100;
	}

	/**
	 * The current entity of which this entity is interacting with. If
	 * the client is not loaded or there is no entity being interacted with,
	 * this will return {@link Npcs#nil()}.
	 *
	 * @return The entity of which is being interacted with.
	 */
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

	private Node[] getBarNodes() {
		final org.powerbot.bot.rt4.client.Actor accessor = getActor();
		if (accessor == null) {
			return null;
		}
		final org.powerbot.bot.rt4.client.LinkedList barList = accessor.getCombatStatusList();
		if (barList == null) {
			return null;
		}
		final Node tail = barList.getSentinel();
		final Node health;
		final Node secondary;
		final Node current;
		current = tail.getNext();
		if (!current.getNext().equals(tail)) {
			secondary = current;
			health = current.getNext();
		} else {
			secondary = null;
			health = current;
		}
		return new Node[]{secondary, health};
	}

	private BarComponent getBarComponent(){
		final Node[] nodes = getBarNodes();
		final Client client = ctx.client();
		if (nodes == null || client == null) {
			return null;
		}
		final CombatStatusData[] data = new CombatStatusData[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] == null || nodes[i].isNull() ||
					!nodes[i].isTypeOf(CombatStatus.class)) {
				data[i] = null;
				continue;
			}
			final CombatStatus status = new CombatStatus(nodes[i].reflector, nodes[i]);
			final org.powerbot.bot.rt4.client.BarComponent barComponent;
			try {
				barComponent = status.getBarComponent();
				return barComponent;
			} catch (final IllegalArgumentException ignored) {
				continue;
			}

		}
		return null;
	}

	private CombatStatusData[] getBarData() {
		final Node[] nodes = getBarNodes();
		final Client client = ctx.client();
		if (nodes == null || client == null) {
			return null;
		}
		final CombatStatusData[] data = new CombatStatusData[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] == null || nodes[i].isNull() ||
					!nodes[i].isTypeOf(CombatStatus.class)) {
				data[i] = null;
				continue;
			}
			final CombatStatus status = new CombatStatus(nodes[i].reflector, nodes[i]);
			final org.powerbot.bot.rt4.client.LinkedList statuses;
			try {
				statuses = status.getList();
			} catch (final IllegalArgumentException ignored) {
				continue;
			}
			if (statuses == null) {
				data[i] = null;
				continue;
			}
			final Node node = statuses.getSentinel().getNext();
			if (node.isNull() || !node.isTypeOf(CombatStatusData.class)) {
				data[i] = null;
				continue;
			}
			data[i] = new CombatStatusData(node.reflector, node);
		}
		return data;
	}
}
