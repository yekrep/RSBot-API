package org.powerbot.os.api;

import org.powerbot.os.api.wrappers.Npc;
import org.powerbot.os.client.Client;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Npcs extends MethodProvider {
	public Npcs(final MethodContext ctx) {
		super(ctx);
	}

	public List<Npc> getLoaded() {
		final List<Npc> r = new CopyOnWriteArrayList<Npc>();
		final Client client = ctx.getClient();
		if (client == null) return r;
		final int[] indices = client.getNpcIndices();
		final org.powerbot.os.client.Npc[] npcs = client.getNpcs();
		if (indices == null || npcs == null) return r;
		final Npc[] arr = new Npc[indices.length];
		int d = 0;
		for (final int k : indices) {
			final org.powerbot.os.client.Npc p = npcs[k];
			if (p != null) arr[d++] = new Npc(ctx, p);
		}
		return new CopyOnWriteArrayList<Npc>(Arrays.copyOf(arr, d));
	}
}
