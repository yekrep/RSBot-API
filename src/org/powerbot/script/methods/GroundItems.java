package org.powerbot.script.methods;

import org.powerbot.client.Client;
import org.powerbot.client.HashTable;
import org.powerbot.client.NodeDeque;
import org.powerbot.client.NodeListCache;
import org.powerbot.client.RSItem;
import org.powerbot.script.internal.wrappers.Deque;
import org.powerbot.script.lang.LocatableIdQuery;
import org.powerbot.script.wrappers.GroundItem;
import org.powerbot.script.wrappers.Tile;

import java.util.ArrayList;
import java.util.List;

public class GroundItems extends LocatableIdQuery<GroundItem> {
	public GroundItems(MethodContext factory) {
		super(factory);
	}

	@Override
	protected List<GroundItem> get() {
		final List<GroundItem> items = new ArrayList<>();

		Client client = ctx.getClient();
		if (client == null) {
			return items;
		}

		HashTable table = client.getRSItemHashTable();
		if (table == null) {
			return items;
		}

		int plane = client.getPlane();
		long id;
		NodeListCache cache;
		NodeDeque deque;

		Tile base = ctx.game.getMapBase();
		if (base == null) {
			return items;
		}
		int bx = base.getX(), by = base.getY();
		for (int x = bx; x < bx + 104; x++) {
			for (int y = by; y < by + 104; y++) {
				id = x | y << 14 | plane << 28;
				cache = (NodeListCache) ctx.game.lookup(table, id);
				if (cache == null || (deque = cache.getNodeList()) == null) {
					continue;
				}
				final Deque<RSItem> itemStack = new Deque<>(deque);
				for (RSItem item = itemStack.getHead(); item != null; item = itemStack.getNext()) {
					items.add(new GroundItem(ctx, new Tile(x, y, plane), item));
				}
			}
		}
		return items;
	}
}
