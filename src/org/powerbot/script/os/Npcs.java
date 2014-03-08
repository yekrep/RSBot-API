package org.powerbot.script.os;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.bot.os.client.Client;

public class Npcs extends BasicQuery<Npc> {
	public Npcs(final ClientContext ctx) {
		super(ctx);
	}

	@Override
	public List<Npc> get() {
		final List<Npc> r = new ArrayList<Npc>();
		final Client client = ctx.client();
		if (client == null) {
			return r;
		}
		final int[] indices = client.getNpcIndices();
		final org.powerbot.bot.os.client.Npc[] npcs = client.getNpcs();
		if (indices == null || npcs == null) {
			return r;
		}
		for (final int k : indices) {
			final org.powerbot.bot.os.client.Npc p = npcs[k];
			if (p != null) {
				r.add(new Npc(ctx, p));
			}
		}
		return r;
	}

	@Override
	public Npc getNil() {
		return new Npc(ctx, null);
	}
}
