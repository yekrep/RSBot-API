package org.powerbot.script.methods;

import org.powerbot.client.*;
import org.powerbot.script.internal.wrappers.Deque;
import org.powerbot.script.wrappers.GroundItem;
import org.powerbot.script.wrappers.Tile;

import java.util.HashSet;
import java.util.Set;

public class GroundItems extends Filtering<GroundItem> {
	public GroundItems(ClientFactory factory) {
		super(factory);
	}

	@Override
	public GroundItem[] list() {
		Set<GroundItem> items = new HashSet<>();

		Client client = ctx.getClient();
		if (client == null) return new GroundItem[0];

		HashTable table = client.getRSItemHashTable();
		if (table == null) return new GroundItem[0];

		int plane = client.getPlane();
		long id;
		NodeListCache cache;
		NodeDeque deque;

		Tile base = ctx.game.getMapBase();
		if (base == null) return new GroundItem[0];
		int bx = base.getX(), by = base.getY();
		for (int x = bx; x < bx + 104; x++) {
			for (int y = by; y < by + 104; y++) {
				id = x | y << 14 | plane << 28;
				cache = (NodeListCache) ctx.game.lookup(table, id);
				if (cache == null || (deque = cache.getNodeList()) == null) continue;
				final Deque<RSItem> itemStack = new Deque<>(deque);
				for (RSItem item = itemStack.getHead(); item != null; item = itemStack.getNext()) {
					items.add(new GroundItem(ctx, new Tile(ctx, x, y, plane), item));
				}
			}
		}
		return items.toArray(new GroundItem[items.size()]);
	}
}
