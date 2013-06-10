package org.powerbot.script.methods;

import java.util.Arrays;

import org.powerbot.client.Client;
import org.powerbot.client.RSPlayer;
import org.powerbot.script.wrappers.Player;

/**
 * {@link Players} is a static utility which provides access to the {@link Player}s in the game.
 * <p/>
 * {@link Player}s are only accessible within mini-map's range.
 *
 * @author Timer
 */
public class Players {
	/**
	 * Returns the game's local player (your player).
	 * Must be logged in to retrieve.
	 * <p/>
	 * Be sure to check for nulls!
	 *
	 * @return the local {@link Player}
	 */
	public static Player getLocal() {
		final Client client = ClientFactory.getFactory().getClient();
		if (client == null) return null;

		final RSPlayer p = client.getMyRSPlayer();
		return p != null ? new Player(p) : null;
	}

	/**
	 * Returns all the {@link Player}s in the region.
	 *
	 * @return an array of all the loaded {@link Player}s
	 */
	public static Player[] getLoaded() {
		final Client client = ClientFactory.getFactory().getClient();
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
}
