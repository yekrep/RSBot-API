package org.powerbot.script.rt6;

import java.util.Arrays;

import org.powerbot.bot.Reflector;
import org.powerbot.bot.rt6.HashTable;
import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.Node;
import org.powerbot.bot.rt6.client.Npc;
import org.powerbot.bot.rt6.client.NpcNode;
import org.powerbot.bot.rt6.client.Player;
import org.powerbot.script.Locatable;
import org.powerbot.script.Tile;
import org.powerbot.script.Validatable;

/**
 * HintArrow
 */
public class HintArrow extends ClientAccessor implements Locatable, Validatable {
	private final org.powerbot.bot.rt6.client.HintArrow arrow;

	public HintArrow(final ClientContext ctx, final org.powerbot.bot.rt6.client.HintArrow arrow) {
		super(ctx);
		this.arrow = arrow;
	}

	public int type() {
		return arrow.getType();
	}

	public int targetId() {
		return arrow.getTargetId();
	}

	public int floor() {
		return arrow.getFloor();
	}

	@Override
	public Tile tile() {
		final Client client = ctx.client();
		if (client == null || arrow.obj.get() == null) {
			return Tile.NIL;
		}

		final int type = type();
		final int target = targetId();
		if (type == -1 || type == 0) {
			return Tile.NIL;
		}
		if (type == 1) {
			org.powerbot.script.rt6.Npc npc = null;
			final Node node = HashTable.lookup(client.getNpcTable(), target, Node.class);
			if (!node.isNull()) {
				final Reflector r = client.reflector;
				if (node.isTypeOf(NpcNode.class)) {
					npc = new org.powerbot.script.rt6.Npc(ctx, new NpcNode(r, node).getNpc());
				} else if (node.isTypeOf(Npc.class)) {
					npc = new org.powerbot.script.rt6.Npc(ctx, new Npc(r, node));
				}
			}
			return npc != null ? npc.tile() : Tile.NIL;
		} else if (type == 2) {
			return ctx.game.mapOffset().derive(arrow.getX() >> 9, arrow.getY() >> 9, floor());
		}
		final Player[] players = client.getPlayers();
		if (type != 10 || target < 0 || target >= players.length) {
			return Tile.NIL;
		}
		final Player localPlayer = players[target];
		if (localPlayer != null) {
			return new org.powerbot.script.rt6.Player(ctx, localPlayer).tile();
		}
		return Tile.NIL;
	}

	public RelativeLocation relative() {
		if (arrow.obj.get() != null) {
			return new RelativeLocation(arrow.getX(), arrow.getY(), arrow.getFloor());
		}
		return RelativeLocation.NIL;
	}

	@Override
	public boolean valid() {
		final Client client = ctx.client();
		if (client == null) {
			return false;
		}
		final org.powerbot.bot.rt6.client.HintArrow[] arr = client.getHintArrows();
		return arrow.obj.get() != null && arr != null && Arrays.asList(arr).contains(arrow);
	}

	@Override
	public int hashCode() {
		final Object i;
		return (i = this.arrow.obj.get()) != null ? System.identityHashCode(i) : 0;
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof HintArrow && arrow.equals(((HintArrow) o).arrow);
	}
}
