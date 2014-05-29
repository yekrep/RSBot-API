package org.powerbot.script.rt6;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.HashTable;
import org.powerbot.bot.rt6.client.NodeListCache;
import org.powerbot.bot.rt6.client.RSItem;
import org.powerbot.bot.rt6.NodeQueue;
import org.powerbot.script.Tile;

public class GroundItems extends GroundItemQuery<GroundItem> {
	public GroundItems(final ClientContext factory) {
		super(factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<GroundItem> get() {
		final List<GroundItem> items = new ArrayList<GroundItem>();

		final Client client = ctx.client();
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

		final Tile base = ctx.game.mapOffset();
		if (base == null) {
			return items;
		}
		final int bx = base.x();
		final int by = base.y();
		for (int x = bx; x < bx + 104; x++) {
			for (int y = by; y < by + 104; y++) {
				id = x | y << 14 | plane << 28;
				cache = org.powerbot.bot.rt6.HashTable.lookup(table, id, NodeListCache.class);
				if (cache.isNull()) {
					continue;
				}
				for (final RSItem item : NodeQueue.get(cache.getNodeList(), RSItem.class)) {
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
	public GroundItem nil() {
		return new GroundItem(ctx, Tile.NIL, new RSItem(ctx.client().reflector, null));
	}
}
