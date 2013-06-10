package org.powerbot.script.methods;

import java.util.HashSet;
import java.util.Set;

import org.powerbot.client.Client;
import org.powerbot.client.HashTable;
import org.powerbot.client.NodeDeque;
import org.powerbot.client.NodeListCache;
import org.powerbot.client.RSItem;
import org.powerbot.script.internal.wrappers.Deque;
import org.powerbot.script.wrappers.GroundItem;
import org.powerbot.script.wrappers.Tile;

public class GroundItems {
	public static GroundItem[] getLoaded() {
		Set<GroundItem> items = new HashSet<>();

		Client client = ClientFactory.getFactory().getClient();
		if (client == null) return new GroundItem[0];

		HashTable table = client.getRSItemHashTable();
		if (table == null) return new GroundItem[0];

		int plane = client.getPlane();
		long id;
		NodeListCache cache;
		NodeDeque deque;

		Tile base = Game.getMapBase();
		if (base == null) return new GroundItem[0];
		int bx = base.getX(), by = base.getY();
		for (int x = bx; x < bx + 104; x++) {
			for (int y = by; y < by + 104; y++) {
				id = x | y << 14 | plane << 28;
				cache = (NodeListCache) Game.lookup(table, id);
				if (cache == null || (deque = cache.getNodeList()) == null) continue;
				final Deque<RSItem> itemStack = new Deque<>(deque);
				for (RSItem item = itemStack.getHead(); item != null; item = itemStack.getNext()) {
					items.add(new GroundItem(new Tile(x, y, plane), item));
				}
			}
		}
		return items.toArray(new GroundItem[items.size()]);
	}
}
