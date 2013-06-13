package org.powerbot.script.methods;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.client.Client;
import org.powerbot.client.RSPlayer;
import org.powerbot.script.lang.LocatableNameQuery;
import org.powerbot.script.wrappers.Player;

/**
 * {@link Players} is a static utility which provides access to the {@link Player}s in the game.
 * <p/>
 * {@link Player}s are only accessible within mini-map's range.
 *
 * @author Timer
 */
public class Players extends LocatableNameQuery<Player> {
	public Players(ClientFactory factory) {
		super(factory);
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
		Client client = ctx.getClient();
		if (client == null) {
			return null;
		}

		final RSPlayer p = client.getMyRSPlayer();
		return p != null ? new Player(ctx, p) : null;
	}

	/**
	 * Returns all the {@link Player}s in the region.
	 *
	 * @return an array of all the loaded {@link Player}s
	 */
	@Override
	protected List<Player> get() {
		final List<Player> items = new ArrayList<>();

		Client client = ctx.getClient();
		if (client == null) {
			return items;
		}

		final int[] indices = client.getRSPlayerIndexArray();
		final RSPlayer[] players = client.getRSPlayerArray();
		if (indices == null || players == null) {
			return items;
		}

		for (final int index : indices) {
			final RSPlayer player = players[index];
			if (player != null) {
				items.add(new Player(ctx, player));
			}
		}

		return items;
	}
}
