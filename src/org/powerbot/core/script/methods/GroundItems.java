package org.powerbot.core.script.methods;

import java.util.HashSet;
import java.util.Set;

import org.powerbot.core.Bot;
import org.powerbot.core.script.internal.wrappers.Deque;
import org.powerbot.core.script.util.Filter;
import org.powerbot.core.script.wrappers.GroundItem;
import org.powerbot.core.script.wrappers.Item;
import org.powerbot.core.script.wrappers.Player;
import org.powerbot.core.script.wrappers.Tile;
import org.powerbot.game.api.util.node.Nodes;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.HashTable;
import org.powerbot.game.client.NodeDeque;
import org.powerbot.game.client.NodeListCache;
import org.powerbot.game.client.RSItem;

public class GroundItems {
	private static final int LOADED_DIST = 104;
	
	public static Set<GroundItem> getLoaded() {
		return getLoaded(LOADED_DIST);
	}

	public static Set<GroundItem> getLoaded(final int range) {
		final Set<GroundItem> items = new HashSet<>();

		final Client client = Bot.client();
		if (client == null) return items;

		final HashTable table = client.getRSItemHashTable();
		if (table == null) return items;

		final Player player = Players.getLocal();
		final Tile location, base;
		if (player == null || (location = player.getLocation()) == null || (base = Game.getMapBase()) == null) {
			return items;
		}
		int _x = location.getX() - base.getX(), _y = location.getY() - base.getY();
		final int plane = client.getPlane();
		long id;
		NodeListCache cache;
		NodeDeque deque;
		for (int x = Math.max(0, _x - range); x < Math.min(LOADED_DIST, _x + range); x++) {
			for (int y = Math.max(0, _y - range); y < Math.min(LOADED_DIST, _y + range); y++) {
				id = x | y << 14 | plane << 28;
				cache = (NodeListCache) Nodes.lookup(table, id);
				if (cache == null || (deque = cache.getNodeList()) == null) continue;
				final Deque<RSItem> itemStack = new Deque<>(deque);
				for (RSItem item = itemStack.getHead(); item != null; item = itemStack.getNext()) {
					items.add(new GroundItem(new Tile(x, y, plane), new Item(item)));
				}
			}
		}
		return items;
	}

	public static Set<GroundItem> getLoaded(final Filter<GroundItem> filter) {
		return getLoaded(LOADED_DIST, filter);

	}

	public static Set<GroundItem> getLoaded(final int range, final Filter<GroundItem> filter) {
		final Set<GroundItem> items = getLoaded(range);
		final Set<GroundItem> set = new HashSet<>(items.size());
		for (final GroundItem item : items) if (filter.accept(item)) set.add(item);
		return set;

	}

	public static GroundItem getNearest(final Filter<GroundItem> filter) {
		return getNearest(LOADED_DIST, filter);
	}

	public static GroundItem getNearest(final int range, final Filter<GroundItem> filter) {
		GroundItem nearest = null;
		double dist = 104d;

		final Player local = Players.getLocal();
		if (local == null) return null;

		final Tile pos = local.getLocation();
		if (pos == null) return null;
		final Set<GroundItem> groundItems = getLoaded(range);
		for (final GroundItem groundItem : groundItems) {
			final double d;
			if (filter.accept(groundItem) && (d = Calculations.distance(pos, groundItem)) < dist) {
				nearest = groundItem;
				dist = d;
			}
		}

		return nearest;
	}
}
