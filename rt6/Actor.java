package org.powerbot.script.rt6;

import java.awt.Point;

import org.powerbot.bot.Reflector;
import org.powerbot.bot.rt6.HashTable;
import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.CombatStatus;
import org.powerbot.bot.rt6.client.CombatStatusData;
import org.powerbot.bot.rt6.client.LinkedListNode;
import org.powerbot.bot.rt6.client.Node;
import org.powerbot.bot.rt6.client.Npc;
import org.powerbot.bot.rt6.client.NpcNode;
import org.powerbot.bot.rt6.client.Player;
import org.powerbot.bot.rt6.client.RelativePosition;
import org.powerbot.script.Filter;
import org.powerbot.script.InteractiveEntity;
import org.powerbot.script.Nameable;
import org.powerbot.script.Tile;

/**
 * Actor
 */
public abstract class Actor extends Interactive implements InteractiveEntity, Nameable {
	public Actor(final ClientContext ctx) {
		super(ctx);
		bounds(-192, 192, -768, 0, -192, 192);
	}

	public static Filter<Actor> areInMotion() {
		return new Filter<Actor>() {
			@Override
			public boolean accept(final Actor actor) {
				return actor.inMotion();
			}
		};
	}

	public static Filter<Actor> areInCombat() {
		return new Filter<Actor>() {
			@Override
			public boolean accept(final Actor actor) {
				return actor.inCombat();
			}
		};
	}

	private static int toPercent(final int ratio) {
		return (int) Math.ceil(ratio * 100d / 255d);
	}

	protected abstract org.powerbot.bot.rt6.client.Actor getAccessor();

	@Override
	public void bounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) {
		boundingModel.set(new BoundingModel(ctx, x1, x2, y1, y2, z1, z2) {
			@Override
			public int x() {
				final RelativeLocation r = relative();
				return (int) r.x();
			}

			@Override
			public int z() {
				final RelativeLocation r = relative();
				return (int) r.z();
			}

			@Override
			public int floor() {
				final RelativeLocation r = relative();
				return r.floor();
			}
		});
	}

	public abstract int combatLevel();

	public int orientation() {
		final org.powerbot.bot.rt6.client.Actor character = getAccessor();
		return character != null ? (630 - character.getOrientation() * 45 / 2048) % 360 : 0;
	}

	public int height() {
		return getAccessor().getHeight();
	}

	public int animation() {
		return getAccessor().getAnimation().getSequence().getId();
	}

	public int stance() {
		return getAccessor().getPassiveAnimation().getSequence().getId();
	}

	public int[] animationQueue() {
		final int[] arr = getAccessor().getAnimationQueue();
		return arr != null ? arr : new int[0];
	}

	public int speed() {
		return getAccessor().getSpeed();
	}

	public boolean inMotion() {
		return speed() > 0;
	}

	public String overheadMessage() {
		final String message = getAccessor().getMessage().getText();
		return message != null ? message : "";
	}

	public Actor interacting() {
		final Actor nil = ctx.npcs.nil();
		final org.powerbot.bot.rt6.client.Actor actor = getAccessor();
		final int index = actor != null ? actor.getInteracting() : -1;
		if (index == -1) {
			return nil;
		}
		final Client client = ctx.client();
		if (client == null) {
			return nil;
		}
		if (index < 32768) {
			final Node node = HashTable.lookup(client.getNpcTable(), index, Node.class);
			if (node == null) {
				return nil;
			}
			final Reflector r = client.reflector;
			if (node.isTypeOf(NpcNode.class)) {
				return new org.powerbot.script.rt6.Npc(ctx, new NpcNode(r, node).getNpc());
			} else if (node.isTypeOf(Npc.class)) {
				return new org.powerbot.script.rt6.Npc(ctx, new Npc(r, node));
			}
			return nil;
		} else {
			final int pos = index - 32768;
			final Player[] arr = client.getPlayers();
			return pos >= 0 && pos < arr.length ? new org.powerbot.script.rt6.Player(ctx, arr[pos]) : nil;
		}
	}

	public int adrenalineRatio() {
		if (!valid()) {
			return -1;
		}
		final CombatStatusData[] data = getBarData();
		if (data == null || data[0] == null) {
			return 0;
		}
		return data[0].getHealthRatio();
	}

	public int healthRatio() {
		if (!valid()) {
			return -1;
		}
		final CombatStatusData[] data = getBarData();
		if (data == null || data[1] == null) {
			return 100;
		}
		return data[1].getHealthRatio();
	}

	public int adrenalinePercent() {
		if (!valid()) {
			return -1;
		}
		final CombatStatusData[] data = getBarData();
		if (data == null || data[0] == null) {
			return 0;
		}
		return toPercent(data[0].getHealthRatio());
	}

	public int healthPercent() {
		if (!valid()) {
			return -1;
		}
		final CombatStatusData[] data = getBarData();
		if (data == null || data[1] == null) {
			return 100;
		}
		return toPercent(data[1].getHealthRatio());
	}

	public boolean inCombat() {
		final Client client = ctx.client();
		if (client == null) {
			return false;
		}
		final CombatStatusData[] data = getBarData();
		return data != null && data[1] != null && data[1].getCycleEnd() < client.getCycle();
	}

	public boolean idle() {
		return animation() == -1 && !inCombat() && !inMotion() && !interacting().valid();
	}

	@Override
	public Tile tile() {
		final org.powerbot.bot.rt6.client.Actor character = getAccessor();
		final RelativeLocation position = relative();
		if (character.isNull() || position == RelativeLocation.NIL) {
			return Tile.NIL;
		}
		return ctx.game.mapOffset().derive((int) position.x() >> 9, (int) position.z() >> 9, position.floor());
	}

	public RelativeLocation relative() {
		final RelativePosition location = getAccessor().getLocation().getRelativePosition();
		if (location.isNull()) {
			return RelativeLocation.NIL;
		}
		return new RelativeLocation(location.getX(), location.getZ(), ctx.game.floor());
	}

	@Override
	public Point nextPoint() {
		final BoundingModel model = boundingModel.get();
		return model != null ? model.nextPoint() : new Point(-1, -1);
	}

	public Point centerPoint() {
		final BoundingModel model = boundingModel.get();
		return model != null ? model.centerPoint() : new Point(-1, -1);
	}

	@Override
	public boolean contains(final Point point) {
		final BoundingModel model = boundingModel.get();
		return model != null && model.contains(point);
	}

	private LinkedListNode[] getBarNodes() {
		final org.powerbot.bot.rt6.client.Actor accessor = getAccessor();
		if (accessor == null) {
			return null;
		}
		final org.powerbot.bot.rt6.client.LinkedList barList = accessor.getCombatStatusList();
		if (barList == null) {
			return null;
		}
		final LinkedListNode tail = barList.getSentinel();
		final LinkedListNode health;
		final LinkedListNode adrenaline;
		final LinkedListNode current;
		current = tail.getNext();
		if (!current.getNext().equals(tail)) {
			adrenaline = current;
			health = current.getNext();
		} else {
			adrenaline = null;
			health = current;
		}

		return new LinkedListNode[]{adrenaline, health};
	}

	private CombatStatusData[] getBarData() {
		final LinkedListNode[] nodes = getBarNodes();
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
			final org.powerbot.bot.rt6.client.LinkedList statuses;
			try {
				statuses = status.getList();
			} catch (final IllegalArgumentException ignored) {
				continue;
			}
			if (statuses == null) {
				data[i] = null;
				continue;
			}

			final LinkedListNode node = statuses.getSentinel().getNext();
			if (node.isNull() || !node.isTypeOf(CombatStatusData.class)) {
				data[i] = null;
				continue;
			}
			data[i] = new CombatStatusData(node.reflector, node);
		}
		return data;
	}

	@Override
	public int hashCode() {
		final org.powerbot.bot.rt6.client.Actor i;
		return (i = getAccessor()) != null ? i.hashCode() : -1;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Actor)) {
			return false;
		}
		final Actor c = (Actor) o;
		final org.powerbot.bot.rt6.client.Actor i;
		return (i = getAccessor()) != null && i.equals(c.getAccessor());
	}
}
