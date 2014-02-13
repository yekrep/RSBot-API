package org.powerbot.os.api.methods;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.powerbot.os.api.ClientAccessor;
import org.powerbot.os.api.ClientContext;
import org.powerbot.os.api.util.Deque;
import org.powerbot.os.api.wrappers.GroundItem;
import org.powerbot.os.api.wrappers.Tile;
import org.powerbot.os.client.Client;
import org.powerbot.os.client.ItemNode;
import org.powerbot.os.client.NodeDeque;

public class GroundItems extends ClientAccessor {
	public GroundItems(final ClientContext ctx) {
		super(ctx);
	}

	public List<GroundItem> get() {
		final Client client = ctx.client();
		return get(client != null ? client.getFloor() : -1);
	}

	public List<GroundItem> get(final int floor) {
		final List<GroundItem> r = new CopyOnWriteArrayList<GroundItem>();
		final Client client = ctx.client();
		final NodeDeque[][][] dequeArray;
		if (client == null || (dequeArray = client.getGroundItems()) == null) {
			return r;
		}
		final NodeDeque[][] rows;
		if (floor > -1 && floor < dequeArray.length) {
			rows = dequeArray[floor];
		} else {
			rows = null;
		}
		if (rows == null) {
			return r;
		}
		final List<GroundItem> list = new LinkedList<GroundItem>();
		final Tile tile = new Tile(client.getOffsetX(), client.getOffsetY(), floor);
		for (int x = 0; x < rows.length; x++) {
			final NodeDeque[] row = rows[x];
			if (row == null) {
				continue;
			}
			for (int y = 0; y < row.length; y++) {
				for (final ItemNode node : new Deque<ItemNode>(row[y], ItemNode.class)) {
					list.add(new GroundItem(ctx, tile.derive(x, y), node));
				}
			}

		}
		return new CopyOnWriteArrayList<GroundItem>(list);
	}
}