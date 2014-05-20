package org.powerbot.script.rt6;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
		final List<Player> items = new ArrayList<Player>();

		final Client client = ctx.client();
		if (client == null) {
			return items;
		}

		final int[] indices = client.getRSPlayerIndexArray();
		final RSPlayer[] players = client.getRSPlayerArray();
		if (indices == null || players == null) {
			return items;
		}

		final Set<RSPlayer> set = new HashSet<RSPlayer>(indices.length);
		for (final int index : indices) {
			final RSPlayer player = players[index];
			if (player.obj.get() != null && !set.contains(player)) {
				items.add(new Player(ctx, player));
				set.add(player);
			}
		}
		set.clear();//help gc

		return items;
	}

	@Override
	public Player nil() {
		return new Player(ctx, null);
	}
}
