package org.powerbot.script.rt4;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.powerbot.bot.rt4.client.BasicObject;
import org.powerbot.bot.rt4.client.Client;
import org.powerbot.bot.rt4.client.Landscape;
import org.powerbot.bot.rt4.client.Tile;

public class Objects extends BasicQuery<GameObject> {
	public Objects(final ClientContext ctx) {
		super(ctx);
	}

	@Override
	public List<GameObject> get() {
		final List<GameObject> r = new CopyOnWriteArrayList<GameObject>();
		final Client client = ctx.client();
		if (client == null) {
			return r;
		}
		final Landscape landscape = client.getLandscape();
		final Tile[][][] tiles;
		final int floor = client.getFloor();
		final Tile[][] rows;
		if (landscape == null || (tiles = landscape.getTiles()) == null ||
				floor < 0 || floor > tiles.length || (rows = tiles[floor]) == null) {
			return r;
		}
		final LinkedList<GameObject> list = new LinkedList<GameObject>();
		for (final Tile[] row : rows) {
			if (row == null) {
				continue;
			}
			for (final Tile tile : row) {
				if (tile == null) {
					continue;
				}
				final int len = Math.max(0, tile.getGameObjectLength());
				final BasicObject[] fo = {tile.getBoundaryObject(), tile.getFloorObject(), tile.getWallObject()};
				final BasicObject[] arr = new BasicObject[3 + len];
				System.arraycopy(fo, 0, arr, 0, 3);
				final org.powerbot.bot.rt4.client.GameObject[] interactive = tile.getGameObjects();
				if (interactive != null) {
					System.arraycopy(interactive, 0, arr, 3, Math.min(len, interactive.length));
				}

				for (final BasicObject o : arr) {
					if (o != null) {
						list.add(new GameObject(ctx, o));
					}
				}
			}
		}
		return list;
	}

	@Override
	public GameObject nil() {
		return new GameObject(ctx, null);
	}
}
