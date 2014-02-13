package org.powerbot.os.api.tools;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.powerbot.os.api.ClientAccessor;
import org.powerbot.os.api.ClientContext;
import org.powerbot.os.bot.client.Client;

public class Npcs extends ClientAccessor {
	public Npcs(final ClientContext ctx) {
		super(ctx);
	}

	public List<Npc> get() {
		final List<Npc> r = new CopyOnWriteArrayList<Npc>();
		final Client client = ctx.client();
		if (client == null) {
			return r;
		}
		final int[] indices = client.getNpcIndices();
		final org.powerbot.os.bot.client.Npc[] npcs = client.getNpcs();
		if (indices == null || npcs == null) {
			return r;
		}
		final Npc[] arr = new Npc[indices.length];
		int d = 0;
		for (final int k : indices) {
			final org.powerbot.os.bot.client.Npc p = npcs[k];
			if (p != null) {
				arr[d++] = new Npc(ctx, p);
			}
		}
		return new CopyOnWriteArrayList<Npc>(Arrays.copyOf(arr, d));
	}
}
