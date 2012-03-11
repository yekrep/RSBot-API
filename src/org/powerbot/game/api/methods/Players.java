package org.powerbot.game.api.methods;

import java.util.HashSet;
import java.util.Set;

import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.Player;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.client.Client;

/**
 * @author Timer
 */
public class Players {
	public static final Filter<Player> ALL_FILTER = new Filter<Player>() {
		public boolean accept(final Player player) {
			return true;
		}
	};

	public Player[] getLoaded() {
		return getLoaded(ALL_FILTER);
	}

	public Player[] getLoaded(final Filter<Player> filter) {
		final Client client = Bot.resolve().client;
		final int[] indices = client.getRSPlayerIndexArray();
		final Object[] playerArray = client.getRSPlayerArray();
		final Set<Player> players = new HashSet<Player>();
		for (final int index : indices) {
			if (index != 0 && playerArray[index] != null) {
				final Player player = new Player(playerArray[index]);
				if (filter.accept(player)) {
					players.add(player);
				}
			}
		}
		return players.toArray(new Player[players.size()]);
	}
}
