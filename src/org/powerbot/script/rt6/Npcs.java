package org.powerbot.script.rt6;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.bot.Reflector;
import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.Node;
import org.powerbot.bot.rt6.client.RSNPC;
import org.powerbot.bot.rt6.client.RSNPCNode;
import org.powerbot.bot.rt6.HashTable;

/**
 * {@link Npcs} is a static utility which provides access to {@link Npc}s in the game.
 * <p/>
 * {@link Npc}s are only accessible within the mini-map's range.
 */
public class Npcs extends MobileIdNameQuery<Npc> {
	public Npcs(final ClientContext factory) {
		super(factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Npc> get() {
		final List<Npc> npcs = new ArrayList<Npc>();
		final Client client = ctx.client();
		if (client == null) {
			return npcs;
		}

		final int[] keys = client.getRSNPCIndexArray();
		final org.powerbot.bot.rt6.client.HashTable table = client.getRSNPCNC();
		if (keys == null || table.isNull()) {
			return npcs;
		}

		final Reflector r = client.reflector;
		for (final int key : keys) {
			final Object o = HashTable.lookup(table, key, Node.class);
			if (r.isTypeOf(o, RSNPCNode.class)) {
				npcs.add(new Npc(ctx, new RSNPCNode(r, o).getRSNPC()));
			} else if (r.isTypeOf(o, RSNPC.class)) {
				npcs.add(new Npc(ctx, new RSNPC(r, o)));
			}
		}

		return npcs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Npc nil() {
		return new Npc(ctx, new RSNPC(ctx.client().reflector, null));
	}
}
