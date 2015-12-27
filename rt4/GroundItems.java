package org.powerbot.script.rt4;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.powerbot.bot.rt4.client.Client;
import org.powerbot.bot.rt4.client.ItemNode;
import org.powerbot.bot.rt4.client.NodeDeque;
import org.powerbot.bot.rt4.NodeQueue;
import org.powerbot.script.Tile;

public class GroundItems extends BasicQuery<GroundItem> {
	public GroundItems(final ClientContext ctx) {
		super(ctx);
	}

	@Override
	public List<GroundItem> get() {
		final Client client = ctx.client();
		return get(client != null ? client.getFloor() : -1);
	}

	private List<GroundItem> get(final int floor) {
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
				for (final ItemNode n : NodeQueue.get(row[y], ItemNode.class)) {
					list.add(new GroundItem(ctx, tile.derive(x, y), n));
				}
			}

		}
		return list;
	}

	@Override
	public GroundItem nil() {
		return new GroundItem(ctx, Tile.NIL, null);
	}
}