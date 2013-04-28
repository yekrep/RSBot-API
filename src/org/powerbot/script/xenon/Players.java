package org.powerbot.script.xenon;

import java.util.Arrays;

import org.powerbot.bot.Bot;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSPlayer;
import org.powerbot.script.xenon.util.Filter;
import org.powerbot.script.xenon.wrappers.Player;
import org.powerbot.script.xenon.wrappers.Tile;

public class Players {
	public static Player getLocal() {
		final Client client = Bot.client();
		if (client == null) return null;

		final RSPlayer p = client.getMyRSPlayer();
		return p != null ? new Player(p) : null;
	}

	public static Player[] getLoaded() {
		final Client client = Bot.client();
		if (client == null) return new Player[0];

		final int[] indices = client.getRSPlayerIndexArray();
		final RSPlayer[] players = client.getRSPlayerArray();
		if (indices == null || players == null) return new Player[0];

		final Player[] loadedPlayers = new Player[indices.length];
		int d = 0;
		for (final int index : indices) {
			final RSPlayer player = players[index];
			if (player != null) loadedPlayers[d++] = new Player(player);
		}

		return Arrays.copyOf(loadedPlayers, d);
	}

	public static Player[] getLoaded(final Filter<Player> filter) {
		final Player[] players = getLoaded();
		final Player[] set = new Player[players.length];
		int d = 0;
		for (final Player player : players) if (filter.accept(player)) set[d++] = player;
		return Arrays.copyOf(set, d);
	}

	public static Player getNearest(final Filter<Player> filter) {
		Player nearest = null;
		double dist = 104d;

		final Player local = Players.getLocal();
		if (local == null) return null;

		final Tile pos = local.getLocation();
		if (pos == null) return null;
		final Player[] players = getLoaded();
		for (final Player player : players) {
			final double d;
			if (filter.accept(player) && (d = Calculations.distance(pos, player)) < dist) {
				nearest = player;
				dist = d;
			}
		}

		return nearest;
	}
}
