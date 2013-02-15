package org.powerbot.core.script.methods;

import java.util.HashSet;
import java.util.Set;

import org.powerbot.core.Bot;
import org.powerbot.core.script.util.Filter;
import org.powerbot.core.script.wrappers.Player;
import org.powerbot.core.script.wrappers.Tile;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSPlayer;

public class Players {
	public static Player getLocal() {
		final Client client = Bot.client();
		if (client == null) return null;

		final RSPlayer p = client.getMyRSPlayer();
		return p != null ? new Player(p) : null;
	}

	public static Set<Player> getLoaded() {
		final Client client = Bot.client();
		if (client == null) return new HashSet<>(0);

		final int[] indices = client.getRSPlayerIndexArray();
		final RSPlayer[] players = client.getRSPlayerArray();
		if (indices == null || players == null) return new HashSet<>(0);

		final Set<Player> loadedPlayers = new HashSet<>(indices.length);
		for (final int index : indices) {
			final RSPlayer player = players[index];
			if (player != null) loadedPlayers.add(new Player(player));
		}

		return loadedPlayers;
	}

	public static Set<Player> getLoaded(final Filter<Player> filter) {
		final Set<Player> players = getLoaded();
		final Set<Player> set = new HashSet<>(players.size());
		for (final Player player : players) if (filter.accept(player)) set.add(player);
		return set;
	}

	public static Player getNearest(final Filter<Player> filter) {
		Player nearest = null;
		double dist = 104d;

		final Player local = Players.getLocal();
		if (local == null) return null;

		final Tile pos = local.getLocation();
		if (pos == null) return null;
		final Set<Player> players = getLoaded();
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
