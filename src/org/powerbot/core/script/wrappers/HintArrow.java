package org.powerbot.core.script.wrappers;

import java.lang.ref.WeakReference;

import org.powerbot.core.Bot;
import org.powerbot.core.script.internal.Nodes;
import org.powerbot.core.script.methods.Game;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSHintArrow;
import org.powerbot.game.client.RSNPC;
import org.powerbot.game.client.RSNPCNode;
import org.powerbot.game.client.RSPlayer;

public class HintArrow implements Locatable {
	private final WeakReference<RSHintArrow> arrow;

	public HintArrow(final RSHintArrow arrow) {
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
		final Client client = Bot.client();
		final RSHintArrow arrow = this.arrow.get();
		if (client == null || arrow == null) return null;

		final int type = getType();
		int target = getTargetId();
		if (type == -1 || type == 0) return null;
		if (type == 1) {
			Npc npc = null;
			final Object node = Nodes.lookup(client.getRSNPCNC(), target);
			if (node != null) {
				if (node instanceof RSNPCNode) {
					npc = new Npc(((RSNPCNode) node).getRSNPC());
				} else if (node instanceof RSNPC) npc = new Npc((RSNPC) node);
			}
			return npc != null ? npc.getLocation() : null;
		} else if (type == 2) {
			final Tile base = Game.getMapBase();
			return base != null ? base.derive(arrow.getX() >> 9, arrow.getY() >> 9, getPlane()) : null;
		}
		final RSPlayer[] players = client.getRSPlayerArray();
		if (type != 10 || target < 0 || target >= players.length) return null;
		final RSPlayer localPlayer = players[target];
		if (localPlayer != null) return new Player(localPlayer).getLocation();
		return null;
	}
}
