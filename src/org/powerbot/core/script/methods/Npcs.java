package org.powerbot.core.script.methods;

import java.util.HashSet;
import java.util.Set;

import org.powerbot.core.Bot;
import org.powerbot.core.script.internal.Nodes;
import org.powerbot.core.script.util.Filter;
import org.powerbot.core.script.wrappers.Npc;
import org.powerbot.core.script.wrappers.Player;
import org.powerbot.core.script.wrappers.Tile;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.HashTable;
import org.powerbot.game.client.RSNPC;
import org.powerbot.game.client.RSNPCNode;

public class Npcs {
	public static Set<Npc> getLoaded() {
		final Client client = Bot.client();
		if (client == null) return new HashSet<>(0);

		final int[] indices = client.getRSNPCIndexArray();
		final HashTable npcTable = client.getRSNPCNC();
		if (indices == null || npcTable == null) return new HashSet<>(0);

		final Set<Npc> npcs = new HashSet<>(indices.length);
		for (final int index : indices) {
			Object npc = Nodes.lookup(npcTable, index);
			if (npc == null) continue;
			if (npc instanceof RSNPCNode) npc = ((RSNPCNode) npc).getRSNPC();
			if (npc instanceof RSNPC) npcs.add(new Npc((RSNPC) npc));
		}

		return npcs;
	}

	public static Set<Npc> getLoaded(final Filter<Npc> filter) {
		final Set<Npc> npcs = getLoaded();
		final Set<Npc> set = new HashSet<>(npcs.size());
		for (final Npc npc : npcs) if (filter.accept(npc)) set.add(npc);
		return set;
	}

	public static Set<Npc> getLoaded(final int... ids) {
		return getLoaded(new Filter<Npc>() {
			@Override
			public boolean accept(final Npc npc) {
				final int npcId = npc.getId();
				for (final int id : ids) if (npcId == id) return true;
				return false;
			}
		});
	}

	public static Npc getNearest(final Filter<Npc> filter) {
		Npc nearest = null;
		double dist = 104d;

		final Player local = Players.getLocal();
		if (local == null) return null;

		final Tile pos = local.getLocation();
		if (pos == null) return null;
		final Set<Npc> npcs = getLoaded();
		for (final Npc npc : npcs) {
			final double d;
			if (filter.accept(npc) && (d = Calculations.distance(pos, npc)) < dist) {
				nearest = npc;
				dist = d;
			}
		}

		return nearest;
	}

	public static Npc getNearest(final int... ids) {
		return getNearest(new Filter<Npc>() {
			@Override
			public boolean accept(final Npc npc) {
				final int npcId = npc.getId();
				for (final int id : ids) if (npcId == id) return true;
				return false;
			}
		});
	}
}
