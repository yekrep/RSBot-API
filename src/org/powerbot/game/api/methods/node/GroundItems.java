package org.powerbot.game.api.methods.node;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.game.api.internal.util.Deque;
import org.powerbot.game.api.internal.util.Nodes;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.GroundItem;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.NodeListCache;
import org.powerbot.game.client.NodeListCacheNodeList;
import org.powerbot.game.client.RSItem;

public class GroundItems {
	public static final Filter<GroundItem> ALL_FILTER = new Filter<GroundItem>() {
		public boolean accept(final GroundItem item) {
			return true;
		}
	};

	/**
	 * Returns all ground items in the loaded area.
	 *
	 * @return All ground items in the loaded area.
	 */
	public static GroundItem[] getLoaded() {
		return getLoaded(104, ALL_FILTER);
	}

	/**
	 * Returns all matching ground items within the provided range.
	 *
	 * @param range  The range (max distance in all directions) in which to check items for.
	 * @param filter Filters out unwanted matches.
	 * @return <tt>GroundItem</tt> array containing all of the items in range.
	 */
	public static GroundItem[] getLoaded(final int range, final Filter<GroundItem> filter) {
		final ArrayList<GroundItem> temp = new ArrayList<GroundItem>();
		final int pX = Players.getLocal().getLocation().x;
		final int pY = Players.getLocal().getLocation().y;
		final int minX = Math.max(0, pX - range), minY = Math.max(0, pY - range);
		final int maxX = Math.min(104, pX + range), maxY = Math.min(104, pY + range);
		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				final GroundItem[] items = getLoadedAt(x, y);
				for (final GroundItem item : items) {
					if (item != null && filter.accept(item)) {
						temp.add(item);
					}
				}
			}
		}
		return temp.toArray(new GroundItem[temp.size()]);
	}

	/**
	 * Returns all the ground items at a tile on the current plane.
	 *
	 * @param x The x position of the tile in the world.
	 * @param y The y position of the tile in the world.
	 * @return An array of the ground items on the specified tile.
	 */
	public static GroundItem[] getLoadedAt(final int x, final int y) {
		if (!Game.isLoggedIn()) {
			return new GroundItem[0];
		}
		final List<GroundItem> groundItems = new ArrayList<GroundItem>();
		final Client client = Bot.resolve().client;
		final Object itemHashTable = client.getRSItemHashTable();
		final int floor = Game.getPlane();
		final int index = x | y << 14 | floor << 28;

		final NodeListCache itemNodeListCache = (NodeListCache) Nodes.lookup(itemHashTable, index);

		if (itemNodeListCache == null || itemNodeListCache.getData() == null) {
			return new GroundItem[0];
		}

		final Deque<RSItem> itemDeque = new Deque<RSItem>(((NodeListCacheNodeList) itemNodeListCache.getData()).getNodeListCacheNodeList());
		for (RSItem item = itemDeque.getHead(); item != null; item = itemDeque.getNext()) {
			groundItems.add(new GroundItem(new Tile(x, y, floor), new Item(item)));
		}
		return groundItems.toArray(new GroundItem[groundItems.size()]);
	}
}
