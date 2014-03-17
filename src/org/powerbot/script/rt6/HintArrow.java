package org.powerbot.script.rt6;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.RSHintArrow;
import org.powerbot.bot.rt6.client.RSNPC;
import org.powerbot.bot.rt6.client.RSNPCNode;
import org.powerbot.bot.rt6.client.RSPlayer;
import org.powerbot.script.Locatable;
import org.powerbot.script.Tile;
import org.powerbot.script.Validatable;

public class HintArrow extends ClientAccessor implements Locatable, Validatable {
	private final WeakReference<RSHintArrow> arrow;

	public HintArrow(final ClientContext ctx, final RSHintArrow arrow) {
		super(ctx);
		this.arrow = new WeakReference<RSHintArrow>(arrow);
	}

	public int type() {
		final RSHintArrow arrow = this.arrow.get();
		return arrow != null ? arrow.getType() : -1;
	}

	public int targetId() {
		final RSHintArrow arrow = this.arrow.get();
		return arrow != null ? arrow.getTargetID() : -1;
	}

	public int floor() {
		final RSHintArrow arrow = this.arrow.get();
		return arrow != null ? arrow.getPlane() : -1;
	}

	@Override
	public Tile tile() {
		final Client client = ctx.client();
		final RSHintArrow arrow = this.arrow.get();
		if (client == null || arrow == null) {
			return Tile.NIL;
		}

		final int type = type();
		final int target = targetId();
		if (type == -1 || type == 0) {
			return Tile.NIL;
		}
		if (type == 1) {
			Npc npc = null;
			final Object node = ctx.game.lookup(client.getRSNPCNC(), target);
			if (node != null) {
				if (node instanceof RSNPCNode) {
					npc = new Npc(ctx, ((RSNPCNode) node).getRSNPC());
				} else if (node instanceof RSNPC) {
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
		final RSHintArrow arrow = this.arrow.get();
		if (arrow != null) {
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

		final RSHintArrow arrow = this.arrow.get();
		final RSHintArrow[] arr = client.getRSHintArrows();
		return arrow != null && arr != null && Arrays.asList(arr).contains(arrow);
	}

	@Override
	public int hashCode() {
		final RSHintArrow i;
		return (i = this.arrow.get()) != null ? System.identityHashCode(i) : 0;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof HintArrow)) {
			return false;
		}
		final HintArrow a = (HintArrow) o;
		final RSHintArrow i;
		return (i = this.arrow.get()) != null && i == a.arrow.get();
	}
}
