package org.powerbot.script.rt6;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.HashTable;
import org.powerbot.bot.rt6.client.RSNPC;
import org.powerbot.bot.rt6.client.RSNPCNode;

/**
 * {@link Npcs} is a static utility which provides access to {@link Npc}s in the game.
 *
 * {@link Npc}s are only accessible within the mini-map's range.
 *
 */
public class Npcs extends MobileIdNameQuery<Npc> {
	public Npcs(final ClientContext factory) {
		super(factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Npc> get() {//TODO: revise
		final List<Npc> items = new ArrayList<Npc>();

		final Client client = ctx.client();
		if (client == null) {
			return items;
		}

		final int[] indices = client.getRSNPCIndexArray();
		final HashTable npcTable = client.getRSNPCNC();
		if (indices == null || npcTable == null) {
			return items;
		}

		for (final int index : indices) {
			Object npc = ctx.game.lookup(npcTable, index);
			if (npc == null) {
				continue;
			}
			if (npc instanceof RSNPCNode) {
				npc = ((RSNPCNode) npc).getRSNPC();
			}
			if (npc instanceof RSNPC) {
				items.add(new Npc(ctx, (RSNPC) npc));
			}
		}

		return items;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Npc nil() {
		return new Npc(ctx, null);
	}
}
