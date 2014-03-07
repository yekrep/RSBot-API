package org.powerbot.script.os.tools;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.powerbot.script.ClientAccessor;
import org.powerbot.script.ClientContext;
import org.powerbot.bot.os.client.Client;

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
		final org.powerbot.bot.os.client.Npc[] npcs = client.getNpcs();
		if (indices == null || npcs == null) {
			return r;
		}
		final Npc[] arr = new Npc[indices.length];
		int d = 0;
		for (final int k : indices) {
			final org.powerbot.bot.os.client.Npc p = npcs[k];
			if (p != null) {
				arr[d++] = new Npc(ctx, p);
			}
		}
		return new CopyOnWriteArrayList<Npc>(Arrays.copyOf(arr, d));
	}
}
