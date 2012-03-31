package org.powerbot.game.api.methods.interactive;

import java.util.HashSet;
import java.util.Set;

import org.powerbot.game.api.RegionTile;
import org.powerbot.game.api.internal.util.Nodes;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.interactive.Npc;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.Node;
import org.powerbot.game.client.RSNPCHolder;
import org.powerbot.game.client.RSNPCNode;
import org.powerbot.game.client.RSNPCNodeHolder;

/**
 * A utility for the access of Npcs.
 *
 * @author Timer
 */
public class Npcs {
	public static final Filter<Npc> ALL_FILTER = new Filter<Npc>() {
		public boolean accept(final Npc player) {
			return true;
		}
	};

	/**
	 * @return An array of the currently loaded Npcs in the game.
	 */
	public static Npc[] getLoaded() {
		return getLoaded(ALL_FILTER);
	}

	/**
	 * @param filter The filtering <code>Filter</code> to accept all the Npcs through.
	 * @return An array of the currently loaded Npcs in the game that are accepted by the provided filter.
	 */
	public static Npc[] getLoaded(final Filter<Npc> filter) {
		final Client client = Bot.resolve().getClient();
		final int[] indices = client.getRSNPCIndexArray();
		final Set<Npc> npcs = new HashSet<Npc>();
		for (final int index : indices) {
			final Node node = Nodes.lookup(client.getRSNPCNC(), index);
			if (node != null && node instanceof RSNPCNode) {
				final Npc npc = new Npc(((RSNPCHolder) ((RSNPCNodeHolder) ((RSNPCNode) node).getData()).getRSNPCNodeHolder()).getRSNPC());
				if (filter.accept(npc)) {
					npcs.add(npc);
				}
			}
		}
		return npcs.toArray(new Npc[npcs.size()]);
	}

	public static Npc getNearest(final Filter<Npc> filter) {
		final Client client = Bot.resolve().getClient();
		final int[] indices = client.getRSNPCIndexArray();
		Npc npc = null;
		double distance = Double.MAX_VALUE;
		final RegionTile position = Players.getLocal().getRegionPosition();
		for (final int index : indices) {
			final Node node = Nodes.lookup(client.getRSNPCNC(), index);
			if (node != null && node instanceof RSNPCNode) {
				final Npc t_npc = new Npc(((RSNPCHolder) ((RSNPCNodeHolder) ((RSNPCNode) node).getData()).getRSNPCNodeHolder()).getRSNPC());
				if (filter.accept(t_npc)) {
					final double dist = Calculations.distance(position, t_npc.getRegionPosition());
					if (dist < distance) {
						distance = dist;
						npc = t_npc;
					}
				}
			}
		}
		return npc;
	}
}
