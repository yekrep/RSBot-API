package org.powerbot.script.rt6;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.RSPlayer;

/**
 * {@link Players} is a utility which provides access to the {@link Player}s in the game.
 * <p/>
 * {@link Player}s are only accessible within mini-map's range.
 */
public class Players extends PlayerQuery<Player> {
	public Players(final ClientContext factory) {
		super(factory);
	}

	/**
	 * Returns the game's local player (your player).
	 *
	 * @return the local {@link Player} or the value of {@link #nil()}
	 */
	public Player local() {
		final Client client = ctx.client();
		return client == null ? nil() : new Player(ctx, client.getMyRSPlayer());
	}

	/**
	 * Returns all the {@link Player}s in the region.
	 *
	 * @return an array of all the loaded {@link Player}s
	 */
	@Override
	protected List<Player> get() {
		final List<Player> players = new ArrayList<Player>();
		final Client client = ctx.client();
		if (client == null) {
			return players;
		}

		final int count = client.getRSPlayerCount();
		final int[] keys = client.getRSPlayerIndexArray();
		final RSPlayer[] arr = client.getRSPlayerArray();
		if (keys == null || arr == null) {
			return players;
		}

		for (int i = 0; i < Math.min(keys.length, count); i++) {
			final int key = keys[count];
			final RSPlayer player = arr[key];
			if (player.isNull()) {
				continue;
			}
			players.add(new Player(ctx, player));
		}
		return players;
	}

	@Override
	public Player nil() {
		return new Player(ctx, null);
	}
}
