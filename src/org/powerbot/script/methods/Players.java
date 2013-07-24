package org.powerbot.script.methods;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.powerbot.client.Client;
import org.powerbot.client.RSPlayer;
import org.powerbot.script.lang.PlayerQuery;
import org.powerbot.script.wrappers.Player;

/**
 * {@link Players} is a utility which provides access to the {@link Player}s in the game.
 * <p/>
 * {@link Player}s are only accessible within mini-map's range.
 *
 * @author Timer
 */
public class Players extends PlayerQuery<Player> {
	public Players(MethodContext factory) {
		super(factory);
	}

	/**
	 * Returns the game's local player (your player).
	 *
	 * @return the local {@link Player} or the value of {@link #getNil()}
	 */
	public Player local() {
		final Client client = ctx.getClient();
		return client == null ? getNil() : new Player(ctx, client.getMyRSPlayer());
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

		final Set<RSPlayer> set = new HashSet<>(indices.length);
		for (final int index : indices) {
			final RSPlayer player = players[index];
			if (player != null && !set.contains(player)) {
				items.add(new Player(ctx, player));
				set.add(player);
			}
		}
		set.clear();//help gc

		return items;
	}

	@Override
	public Player getNil() {
		return new Player(ctx, null);
	}
}
