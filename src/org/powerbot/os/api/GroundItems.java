package org.powerbot.os.api;

import org.powerbot.os.api.util.Deque;
import org.powerbot.os.api.wrappers.GroundItem;
import org.powerbot.os.api.wrappers.Tile;
import org.powerbot.os.client.Client;
import org.powerbot.os.client.ItemNode;
import org.powerbot.os.client.NodeDeque;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GroundItems extends MethodProvider {
	public GroundItems(final MethodContext ctx) {
		super(ctx);
	}

	public List<GroundItem> getLoaded() {
		final Client client = ctx.getClient();
		return getLoaded(client != null ? client.getFloor() : -1);
	}

	public List<GroundItem> getLoaded(final int floor) {
		final List<GroundItem> r = new CopyOnWriteArrayList<GroundItem>();
		final Client client = ctx.getClient();
		if (client == null) return r;
		final NodeDeque[][][] dequeArray = client.getGroundItems();
		final NodeDeque[][] rows;
		if (floor > -1 && floor < dequeArray.length) {
			rows = dequeArray[floor];
		} else {
			rows = null;
		}
		if (rows == null) return r;
		final List<GroundItem> list = new LinkedList<GroundItem>();
		final Tile tile = new Tile(client.getOffsetX(), client.getOffsetY(), floor);
		for (int x = 0; x < rows.length; x++) {
			final NodeDeque[] row = rows[x];
			if (row == null) continue;
			for (int y = 0; y < row.length; y++) {
				final NodeDeque nodeDeque = row[y];
				if (nodeDeque == null) continue;
				final Deque<ItemNode> deque = new Deque<ItemNode>(nodeDeque, ItemNode.class);
				for (final ItemNode node : deque) {
					list.add(new GroundItem(tile.derive(x, y), node));
				}
			}

		}
		return new CopyOnWriteArrayList<GroundItem>(list);
	}
}