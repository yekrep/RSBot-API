package org.powerbot.os.api;

import org.powerbot.os.api.wrappers.Player;
import org.powerbot.os.client.Client;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Players extends MethodProvider {
	public Players(final MethodContext ctx) {
		super(ctx);
	}

	public List<Player> getLoaded() {
		final List<Player> r = new CopyOnWriteArrayList<Player>();
		final Client client = ctx.getClient();
		if (client == null) return r;
		final int[] indices = client.getPlayerIndices();
		final org.powerbot.os.client.Player[] players = client.getPlayers();
		if (indices == null || players == null) return r;
		final Player[] arr = new Player[indices.length];
		int d = 0;
		for (final int k : indices) {
			final org.powerbot.os.client.Player p = players[k];
			if (p != null) arr[d++] = new Player(ctx, p);
		}
		return new CopyOnWriteArrayList<Player>(Arrays.copyOf(arr, d));
	}

	public Player getLocal() {
		final Player r = new Player(ctx, null);
		final Client client = ctx.getClient();
		if (client == null) return r;
		return new Player(ctx, client.getPlayer());
	}
}
