package org.powerbot.script.methods;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.powerbot.client.Client;
import org.powerbot.client.HashTable;
import org.powerbot.client.NodeDeque;
import org.powerbot.client.NodeListCache;
import org.powerbot.client.RSItem;
import org.powerbot.script.internal.wrappers.Deque;
import org.powerbot.script.util.Filter;
import org.powerbot.script.wrappers.GroundItem;
import org.powerbot.script.wrappers.Item;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;

public class GroundItems extends WorldImpl {
	private static final int LOADED_DIST = 104;

	public GroundItems(World world) {
		super(world);
	}

	public GroundItem[] getLoaded() {
		return getLoaded(LOADED_DIST);
	}

	public GroundItem[] getLoaded(final int _x, final int _y, final int range) {
		final Set<GroundItem> items = new HashSet<>();

		final Client client = world.getClient();
		if (client == null) return new GroundItem[0];

		final HashTable table = client.getRSItemHashTable();
		if (table == null) return new GroundItem[0];

		final int plane = client.getPlane();
		long id;
		NodeListCache cache;
		NodeDeque deque;
		for (int x = _x - range; x <= _x + range; x++) {
			for (int y = _y - range; y <= _y + range; y++) {
				id = x | y << 14 | plane << 28;
				cache = (NodeListCache) world.game.lookup(table, id);
				if (cache == null || (deque = cache.getNodeList()) == null) continue;
				final Deque<RSItem> itemStack = new Deque<>(deque);
				for (RSItem item = itemStack.getHead(); item != null; item = itemStack.getNext()) {
					items.add(new GroundItem(world, new Tile(world, x, y, plane), new Item(world, item)));
				}
			}
		}
		return items.toArray(new GroundItem[items.size()]);
	}

	public GroundItem[] getLoaded(final int range) {
		final Player player = world.players.getLocal();
		final Tile location;
		if (player == null || (location = player.getLocation()) == null) {
			return new GroundItem[0];
		}

		final int x = location.getX(), y = location.getY();
		return getLoaded(x, y, range);
	}

	public GroundItem[] getLoaded(final Filter<GroundItem> filter) {
		return getLoaded(LOADED_DIST, filter);
	}

	public GroundItem[] getLoaded(final int range, final Filter<GroundItem> filter) {
		final GroundItem[] items = getLoaded(range);
		final GroundItem[] set = new GroundItem[items.length];
		int d = 0;
		for (final GroundItem item : items) if (filter.accept(item)) set[d++] = item;
		return Arrays.copyOf(set, d);
	}

	public GroundItem getNearest(final Filter<GroundItem> filter) {
		return getNearest(LOADED_DIST, filter);
	}

	public GroundItem getNearest(final int range, final Filter<GroundItem> filter) {
		GroundItem nearest = null;
		double dist = 104d;

		final Player local = world.players.getLocal();
		if (local == null) return null;

		final Tile pos = local.getLocation();
		if (pos == null) return null;
		final GroundItem[] groundItems = getLoaded(range);
		for (final GroundItem groundItem : groundItems) {
			final double d;
			if (filter.accept(groundItem) && (d = world.movement.distance(pos, groundItem)) < dist) {
				nearest = groundItem;
				dist = d;
			}
		}

		return nearest;
	}
}
