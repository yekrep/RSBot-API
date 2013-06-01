package org.powerbot.script.methods;

import java.util.Arrays;

import org.powerbot.client.Client;
import org.powerbot.client.RSPlayer;
import org.powerbot.script.util.Filter;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;

/**
 * {@link Players} is a utility which provides access to the {@link Player}s in the game.
 * <p/>
 * {@link Player}s are only accessible within mini-map's range.
 *
 * @author Timer
 */
public class Players extends WorldImpl {
	public Players(World world) {
		super(world);
	}

	/**
	 * Returns the game's local player (your player).
	 * Must be logged in to retrieve.
	 * <p/>
	 * Be sure to check for nulls!
	 *
	 * @return the local {@link Player}
	 */
	public Player getLocal() {
		final Client client = world.getClient();
		if (client == null) return null;

		final RSPlayer p = client.getMyRSPlayer();
		return p != null ? new Player(world, p) : null;
	}

	/**
	 * Returns all the {@link Player}s in the region.
	 *
	 * @return an array of all the loaded {@link Player}s
	 */
	public Player[] getLoaded() {
		final Client client = world.getClient();
		if (client == null) return new Player[0];

		final int[] indices = client.getRSPlayerIndexArray();
		final RSPlayer[] players = client.getRSPlayerArray();
		if (indices == null || players == null) return new Player[0];

		final Player[] loadedPlayers = new Player[indices.length];
		int d = 0;
		for (final int index : indices) {
			final RSPlayer player = players[index];
			if (player != null) loadedPlayers[d++] = new Player(world, player);
		}

		return Arrays.copyOf(loadedPlayers, d);
	}

	/**
	 * Returns all the {@link Player}s in the region accepted by the provided {@link Filter}.
	 *
	 * @param filter the {@link Filter} by which to accept {@link Player}s
	 * @return an array of the filtered {@link Player}s
	 */
	public Player[] getLoaded(final Filter<Player> filter) {
		final Player[] players = getLoaded();
		final Player[] set = new Player[players.length];
		int d = 0;
		for (final Player player : players) if (filter.accept(player)) set[d++] = player;
		return Arrays.copyOf(set, d);
	}

	/**
	 * Returns the nearest {@link Player} in the region accepted by the {@link Filter}
	 *
	 * @param filter the {@link Filter} by which to accept {@link Player}s
	 * @return the {@link Player} nearest to the local player accepted by the filter
	 */
	public Player getNearest(final Filter<Player> filter) {
		Player nearest = null;
		double dist = 104d;

		final Player local = getLocal();
		if (local == null) return null;

		final Tile pos = local.getLocation();
		if (pos == null) return null;
		final Player[] players = getLoaded();
		for (final Player player : players) {
			final double d;
			if (filter.accept(player) && (d = world.movement.distance(pos, player)) < dist) {
				nearest = player;
				dist = d;
			}
		}

		return nearest;
	}
}
