package org.powerbot.script.rt6;

import java.awt.Point;

import org.powerbot.bot.Reflector;
import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.CombatStatus;
import org.powerbot.bot.rt6.client.CombatStatusData;
import org.powerbot.bot.rt6.client.LinkedListNode;
import org.powerbot.bot.rt6.client.RSCharacter;
import org.powerbot.bot.rt6.client.RSInteractableData;
import org.powerbot.bot.rt6.client.RSInteractableLocation;
import org.powerbot.bot.rt6.client.RSNPC;
import org.powerbot.bot.rt6.client.RSNPCNode;
import org.powerbot.bot.rt6.client.RSPlayer;
import org.powerbot.bot.rt6.tools.HashTable;
import org.powerbot.script.Drawable;
import org.powerbot.script.Filter;
import org.powerbot.script.Locatable;
import org.powerbot.script.Nameable;
import org.powerbot.script.Tile;

public abstract class Actor extends Interactive implements Nameable, Locatable, Drawable {
	public Actor(final ClientContext ctx) {
		super(ctx);
		bounds(-192, 192, -768, 0, -192, 192);
	}

	protected abstract RSCharacter getAccessor();

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
		});
	}

	public abstract int combatLevel();

	public int orientation() {
		final RSCharacter character = getAccessor();
		return character != null ? (630 - character.getOrientation() * 45 / 2048) % 360 : 0;
	}

	public int height() {
		return getAccessor().getHeight();
	}

	public int animation() {
		return getAccessor().getAnimation().getSequence().getID();
	}

	public int stance() {
		return getAccessor().getPassiveAnimation().getSequence().getID();
	}

	public int[] animationQueue() {
		final int[] arr = getAccessor().getAnimationQueue();
		return arr != null ? arr : new int[0];
	}

	public int speed() {
		return getAccessor().isMoving();
	}

	public boolean inMotion() {
		return speed() > 0;
	}

	public static Filter<Actor> areInMotion() {
		return new Filter<Actor>() {
			@Override
			public boolean accept(final Actor actor) {
				return actor.inMotion();
			}
		};
	}

	public String overheadMessage() {
		final String message = getAccessor().getMessageData().getMessage();
		return message != null ? message : "";
	}

	public Actor interacting() {
		final Actor nil = ctx.npcs.nil();
		final RSCharacter actor = getAccessor();
		final int index = actor != null ? actor.getInteracting() : -1;
		if (index == -1) {
			return nil;
		}
		final Client client = ctx.client();
		if (client == null) {
			return nil;
		}
		if (index < 32768) {
			final Object node = HashTable.lookup(client.getRSNPCNC(), index);
			if (node == null) {
				return nil;
			}
			final Reflector r = client.reflector;
			if (r.isTypeOf(node, RSNPCNode.class)) {
				return new Npc(ctx, new RSNPCNode(r, node).getRSNPC());
			} else if (r.isTypeOf(node, RSNPC.class)) {
				return new Npc(ctx, new RSNPC(r, node));
			}
			return nil;
		} else {
			final int pos = index - 32768;
			final RSPlayer[] arr = client.getRSPlayerArray();
			return pos >= 0 && pos < arr.length ? new Player(ctx, arr[pos]) : nil;
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
		return data[0].getHPRatio();
	}

	public int healthRatio() {
		if (!valid()) {
			return -1;
		}
		final CombatStatusData[] data = getBarData();
		if (data == null || data[1] == null) {
			return 100;
		}
		return data[1].getHPRatio();
	}

	public int adrenalinePercent() {
		if (!valid()) {
			return -1;
		}
		final CombatStatusData[] data = getBarData();
		if (data == null || data[0] == null) {
			return 0;
		}
		return toPercent(data[0].getHPRatio());
	}

	public int healthPercent() {
		if (!valid()) {
			return -1;
		}
		final CombatStatusData[] data = getBarData();
		if (data == null || data[1] == null) {
			return 100;
		}
		return toPercent(data[1].getHPRatio());
	}

	public boolean inCombat() {
		final Client client = ctx.client();
		if (client == null) {
			return false;
		}
		final CombatStatusData[] data = getBarData();
		return data != null && data[1] != null && data[1].getLoopCycleStatus() < client.getLoopCycle();
	}

	public boolean idle() {
		return animation() == -1 && !inCombat() && !inMotion() && !interacting().valid();
	}

	public static Filter<Actor> areInCombat() {
		return new Filter<Actor>() {
			@Override
			public boolean accept(final Actor actor) {
				return actor.inCombat();
			}
		};
	}

	@Override
	public Tile tile() {
		final RSCharacter character = getAccessor();
		final RelativeLocation position = relative();
		if (character != null && position != RelativeLocation.NIL) {
			return ctx.game.mapOffset().derive((int) position.x() >> 9, (int) position.z() >> 9, character.getPlane());
		}
		return Tile.NIL;
	}

	public RelativeLocation relative() {
		final RSCharacter character = getAccessor();
		final RSInteractableData data = character != null ? character.getData() : null;
		final RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) {
			return new RelativeLocation(location.getX(), location.getY());
		}
		return RelativeLocation.NIL;
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
		final RSCharacter accessor = getAccessor();
		if (accessor == null) {
			return null;
		}
		final org.powerbot.bot.rt6.client.LinkedList barList = accessor.getCombatStatusList();
		if (barList == null) {
			return null;
		}
		final LinkedListNode tail = barList.getTail();
		final LinkedListNode health;
		final LinkedListNode adrenaline;
		final LinkedListNode current;
		current = tail.getNext();
		if (current.getNext() != tail) {
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
			if (nodes[i] == null || !nodes[i].isTypeOf(CombatStatus.class)) {
				data[i] = null;
				continue;
			}
			final CombatStatus status = (CombatStatus) nodes[i];
			final org.powerbot.bot.rt6.client.LinkedList statuses = status.getData();
			if (statuses == null) {
				data[i] = null;
				continue;
			}

			final LinkedListNode node = statuses.getTail().getNext();
			if (node == null || !node.isTypeOf(CombatStatusData.class)) {
				data[i] = null;
				continue;
			}
			data[i] = (CombatStatusData) node;
		}
		return data;
	}

	private int toPercent(final int ratio) {
		return (int) Math.ceil((ratio * 100d) / 255);
	}

	@Override
	public int hashCode() {
		final RSCharacter i;
		return (i = getAccessor()) != null ? System.identityHashCode(i) : 0;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Actor)) {
			return false;
		}
		final Actor c = (Actor) o;
		final RSCharacter i;
		return (i = getAccessor()) != null && i == c.getAccessor();
	}
}
