package org.powerbot.script.rs3.tools;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.bot.rs3.client.Client;
import org.powerbot.bot.rs3.client.HashTable;
import org.powerbot.bot.rs3.client.NodeDeque;
import org.powerbot.bot.rs3.client.NodeListCache;
import org.powerbot.bot.rs3.client.RSItem;
import org.powerbot.bot.rs3.tools.Deque;
import org.powerbot.script.lang.GroundItemQuery;

public class GroundItems extends GroundItemQuery<GroundItem> {
	public GroundItems(final MethodContext factory) {
		super(factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<GroundItem> get() {
		final List<GroundItem> items = new ArrayList<GroundItem>();

		final Client client = ctx.getClient();
		if (client == null) {
			return items;
		}

		final HashTable table = client.getRSItemHashTable();
		if (table == null) {
			return items;
		}

		final int plane = client.getPlane();
		long id;
		NodeListCache cache;
		NodeDeque deque;

		final Tile base = ctx.game.getMapBase();
		if (base == null) {
			return items;
		}
		final int bx = base.getX();
		final int by = base.getY();
		for (int x = bx; x < bx + 104; x++) {
			for (int y = by; y < by + 104; y++) {
				id = x | y << 14 | plane << 28;
				cache = (NodeListCache) ctx.game.lookup(table, id);
				if (cache == null || (deque = cache.getNodeList()) == null) {
					continue;
				}
				final Deque<RSItem> itemStack = new Deque<RSItem>(deque, RSItem.class);
				for (RSItem item = itemStack.getHead(); item != null; item = itemStack.getNext()) {
					items.add(new GroundItem(ctx, new Tile(x, y, plane), item));
				}
			}
		}
		return items;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroundItem getNil() {
		return new GroundItem(ctx, Tile.NIL, null);
	}
}
