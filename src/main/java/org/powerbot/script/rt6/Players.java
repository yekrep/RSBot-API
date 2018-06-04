package org.powerbot.script.rt6;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.Player;

/**
 * Players
 * {@link Players} is a utility which provides access to the {@link org.powerbot.script.rt6.Player}s in the game.
 * 
 * {@link org.powerbot.script.rt6.Player}s are only accessible within mini-map's range.
 */
public class Players extends PlayerQuery<org.powerbot.script.rt6.Player> {
	public Players(final ClientContext factory) {
		super(factory);
	}

	/**
	 * Returns the game's local player (your player).
	 *
	 * @return the local {@link org.powerbot.script.rt6.Player} or the value of {@link #nil()}
	 */
	public org.powerbot.script.rt6.Player local() {
		final Client client = ctx.client();
		return client == null ? nil() : new org.powerbot.script.rt6.Player(ctx, client.getPlayer());
	}

	/**
	 * Returns all the {@link org.powerbot.script.rt6.Player}s in the region.
	 *
	 * @return an array of all the loaded {@link org.powerbot.script.rt6.Player}s
	 */
	@Override
	protected List<org.powerbot.script.rt6.Player> get() {
		final List<org.powerbot.script.rt6.Player> players = new ArrayList<org.powerbot.script.rt6.Player>();
		final Client client = ctx.client();
		if (client == null) {
			return players;
		}

		final int count = client.getPlayerCount();
		final int[] keys = client.getPlayerIndices();
		final Player[] arr = client.getPlayers();
		if (keys == null || arr == null) {
			return players;
		}
		for (int i = 0; i < Math.min(Math.min(keys.length, arr.length), count); i++) {
			final int key = keys[i];
			final Player player = arr[key];
			if (!player.isNull()) {
				players.add(new org.powerbot.script.rt6.Player(ctx, player));
			}
		}
		return players;
	}

	@Override
	public org.powerbot.script.rt6.Player nil() {
		return new org.powerbot.script.rt6.Player(ctx, new Player(ctx.client().reflector, null));
	}
}
