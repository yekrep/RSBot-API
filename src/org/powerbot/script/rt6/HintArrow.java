package org.powerbot.script.rt6;

import java.util.Arrays;

import org.powerbot.bot.Reflector;
import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.RSHintArrow;
import org.powerbot.bot.rt6.client.RSNPC;
import org.powerbot.bot.rt6.client.RSNPCNode;
import org.powerbot.bot.rt6.client.RSPlayer;
import org.powerbot.bot.rt6.tools.HashTable;
import org.powerbot.script.Locatable;
import org.powerbot.script.Tile;
import org.powerbot.script.Validatable;

public class HintArrow extends ClientAccessor implements Locatable, Validatable {
	private final RSHintArrow arrow;

	public HintArrow(final ClientContext ctx, final RSHintArrow arrow) {
		super(ctx);
		this.arrow = arrow;
	}

	public int type() {
		return arrow.getType();
	}

	public int targetId() {
		return arrow.getTargetID();
	}

	public int floor() {
		return arrow.getPlane();
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
			Npc npc = null;
			final Object node = HashTable.lookup(client.getRSNPCNC(), target);
			if (node != null) {
				final Reflector r = client.reflector;
				if (r.isTypeOf(node, RSNPCNode.class)) {
					npc = new Npc(ctx, ((RSNPCNode) node).getRSNPC());
				} else if (r.isTypeOf(node, RSNPC.class)) {
					npc = new Npc(ctx, (RSNPC) node);
				}
			}
			return npc != null ? npc.tile() : Tile.NIL;
		} else if (type == 2) {
			return ctx.game.mapOffset().derive(arrow.getX() >> 9, arrow.getY() >> 9, floor());
		}
		final RSPlayer[] players = client.getRSPlayerArray();
		if (type != 10 || target < 0 || target >= players.length) {
			return Tile.NIL;
		}
		final RSPlayer localPlayer = players[target];
		if (localPlayer != null) {
			return new Player(ctx, localPlayer).tile();
		}
		return Tile.NIL;
	}

	public RelativeLocation relative() {
		if (arrow.obj.get() != null) {
			return new RelativeLocation(arrow.getX(), arrow.getY());
		}
		return RelativeLocation.NIL;
	}

	@Override
	public boolean valid() {
		final Client client = ctx.client();
		if (client == null) {
			return false;
		}
		final RSHintArrow[] arr = client.getRSHintArrows();
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
