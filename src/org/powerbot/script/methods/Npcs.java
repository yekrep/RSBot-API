package org.powerbot.script.methods;

import java.util.Arrays;

import org.powerbot.client.Client;
import org.powerbot.client.HashTable;
import org.powerbot.client.RSNPC;
import org.powerbot.client.RSNPCNode;
import org.powerbot.script.wrappers.Npc;

/**
 * {@link Npcs} is a static utility which provides access to {@link Npc}s in the game.
 * <p/>
 * {@link Npc}s are only accessible within the mini-map's range.
 *
 * @author Timer
 */
public class Npcs extends Filtering<Npc> {
	public Npcs(ClientFactory factory) {
		super(factory);
	}

	/**
	 * Returns all the {@link Npc}s in the region.
	 *
	 * @return an array of the loaded {@link Npc}s
	 */
	@Override
	public Npc[] list() {
		Client client = ctx.getClient();
		if (client == null) {
			return new Npc[0];
		}

		final int[] indices = client.getRSNPCIndexArray();
		final HashTable npcTable = client.getRSNPCNC();
		if (indices == null || npcTable == null) {
			return new Npc[0];
		}

		final Npc[] npcs = new Npc[indices.length];
		int d = 0;
		for (final int index : indices) {
			Object npc = ctx.game.lookup(npcTable, index);
			if (npc == null) {
				continue;
			}
			if (npc instanceof RSNPCNode) {
				npc = ((RSNPCNode) npc).getRSNPC();
			}
			if (npc instanceof RSNPC) {
				npcs[d++] = new Npc(ctx, (RSNPC) npc);
			}
		}

		return Arrays.copyOf(npcs, d);
	}
}
