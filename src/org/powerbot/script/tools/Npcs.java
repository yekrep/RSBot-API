package org.powerbot.script.tools;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.bot.client.Client;
import org.powerbot.bot.client.HashTable;
import org.powerbot.bot.client.RSNPC;
import org.powerbot.bot.client.RSNPCNode;
import org.powerbot.script.lang.BasicNamedQuery;

/**
 * {@link Npcs} is a static utility which provides access to {@link Npc}s in the game.
 * <p/>
 * {@link Npc}s are only accessible within the mini-map's range.
 *
 * @author Timer
 */
public class Npcs extends BasicNamedQuery<Npc> {
	public Npcs(final MethodContext factory) {
		super(factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Npc> get() {
		final List<Npc> items = new ArrayList<Npc>();

		final Client client = ctx.getClient();
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
	public Npc getNil() {
		return new Npc(ctx, null);
	}
}
