package org.powerbot.script.rt6;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.bot.Reflector;
import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.Node;
import org.powerbot.bot.rt6.client.Npc;
import org.powerbot.bot.rt6.client.NpcNode;
import org.powerbot.bot.rt6.HashTable;

/**
 * {@link Npcs} is a static utility which provides access to {@link org.powerbot.script.rt6.Npc}s in the game.
 * <p/>
 * {@link org.powerbot.script.rt6.Npc}s are only accessible within the mini-map's range.
 */
public class Npcs extends MobileIdNameQuery<org.powerbot.script.rt6.Npc> {
	public Npcs(final ClientContext factory) {
		super(factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<org.powerbot.script.rt6.Npc> get() {
		final List<org.powerbot.script.rt6.Npc> npcs = new ArrayList<org.powerbot.script.rt6.Npc>();
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
			if (r.isTypeOf(o, NpcNode.class)) {
				npcs.add(new org.powerbot.script.rt6.Npc(ctx, new NpcNode(r, o).getNpc()));
			} else if (r.isTypeOf(o, Npc.class)) {
				npcs.add(new org.powerbot.script.rt6.Npc(ctx, new Npc(r, o)));
			}
		}

		return npcs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.powerbot.script.rt6.Npc nil() {
		return new org.powerbot.script.rt6.Npc(ctx, new Npc(ctx.client().reflector, null));
	}
}
