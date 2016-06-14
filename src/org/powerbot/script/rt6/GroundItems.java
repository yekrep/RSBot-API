package org.powerbot.script.rt6;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.bot.rt6.NodeQueue;
import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.HashTable;
import org.powerbot.bot.rt6.client.ItemNode;
import org.powerbot.bot.rt6.client.NodeListCache;
import org.powerbot.script.Tile;

/**
 * GroundItems
 */
public class GroundItems extends GroundItemQuery<GroundItem> {
	public GroundItems(final ClientContext factory) {
		super(factory);
	}

	public GroundItemQuery<GroundItem> select(final int radius) {
		return select(get(radius));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<GroundItem> get() {
		return get(104);
	}

	protected List<GroundItem> get(int radius) {
		if (radius < 1) {
			radius = 110;
		}

		final List<GroundItem> items = new ArrayList<GroundItem>();

		final Client client = ctx.client();
		if (client == null) {
			return items;
		}

		final HashTable table = client.getItemTable();
		if (table.isNull()) {
			return items;
		}
		final int plane = client.getFloor();
		long id;
		NodeListCache cache;

		final Tile base = ctx.game.mapOffset();
		final Tile player = ctx.players.local().tile();
		if (base == Tile.NIL || player == Tile.NIL || !player.matrix(ctx).valid()) {
			return items;
		}
		final int bx = base.x(), mx = bx + 103,
				by = base.y(), my = by + 103;
		for (int x = Math.max(bx, player.x() - radius); x <= Math.min(mx, player.x() + radius); x++) {
			for (int y = Math.max(by, player.y() - radius); y <= Math.min(my, player.y() + radius); y++) {
				id = x | y << 14 | plane << 28;
				cache = org.powerbot.bot.rt6.HashTable.lookup(table, id, NodeListCache.class);
				if (cache.isNull()) {
					continue;
				}
				for (final ItemNode item : NodeQueue.get(cache.getDeque(), ItemNode.class)) {
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
		return new GroundItem(ctx, Tile.NIL, new ItemNode(ctx.client().reflector, null));
	}
}
