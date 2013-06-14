package org.powerbot.script.wrappers;

import org.powerbot.client.Client;
import org.powerbot.client.RSHintArrow;
import org.powerbot.client.RSNPC;
import org.powerbot.client.RSNPCNode;
import org.powerbot.client.RSPlayer;
import org.powerbot.script.lang.Locatable;
import org.powerbot.script.lang.Validatable;
import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.methods.ClientLink;

import java.lang.ref.WeakReference;
import java.util.Arrays;

public class HintArrow extends ClientLink implements Locatable, Validatable {
	private final WeakReference<RSHintArrow> arrow;

	public HintArrow(ClientFactory ctx, final RSHintArrow arrow) {
		super(ctx);
		this.arrow = new WeakReference<>(arrow);
	}

	public int getType() {
		final RSHintArrow arrow = this.arrow.get();
		return arrow != null ? arrow.getType() : -1;
	}

	public int getTargetId() {
		final RSHintArrow arrow = this.arrow.get();
		return arrow != null ? arrow.getTargetID() : -1;
	}

	public int getPlane() {
		final RSHintArrow arrow = this.arrow.get();
		return arrow != null ? arrow.getPlane() : -1;
	}

	@Override
	public Tile getLocation() {
		Client client = ctx.getClient();
		final RSHintArrow arrow = this.arrow.get();
		if (client == null || arrow == null) {
			return null;
		}

		final int type = getType();
		int target = getTargetId();
		if (type == -1 || type == 0) {
			return null;
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
			return npc != null ? npc.getLocation() : null;
		} else if (type == 2) {
			final Tile base = ctx.game.getMapBase();
			return base != null ? base.derive(arrow.getX() >> 9, arrow.getY() >> 9, getPlane()) : null;
		}
		final RSPlayer[] players = client.getRSPlayerArray();
		if (type != 10 || target < 0 || target >= players.length) {
			return null;
		}
		final RSPlayer localPlayer = players[target];
		if (localPlayer != null) {
			return new Player(ctx, localPlayer).getLocation();
		}
		return null;
	}

	@Override
	public boolean isValid() {
		Client client = ctx.getClient();
		if (client == null) {
			return false;
		}

		final RSHintArrow arrow = this.arrow.get();
		final RSHintArrow[] arr = client.getRSHintArrows();
		return arrow != null && arr != null && Arrays.asList(arr).contains(arrow);
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
