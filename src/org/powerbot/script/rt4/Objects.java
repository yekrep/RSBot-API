package org.powerbot.script.rt4;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.powerbot.bot.ReflectProxy;
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
		final HashSet<GameObject> set = new HashSet<GameObject>();
		for (final Tile[] row : rows) {
			if (row == null) {
				continue;
			}
			for (final Tile tile : row) {
				if (tile.obj.get() == null) {
					continue;
				}
				final int len = Math.max(0, tile.getGameObjectLength());
				final ReflectProxy[] fo = {tile.getBoundaryObject(), tile.getFloorObject(), tile.getWallObject()};
				final ReflectProxy[] arr = new ReflectProxy[3 + len];
				System.arraycopy(fo, 0, arr, 0, 3);
				final org.powerbot.bot.rt4.client.GameObject[] interactive = tile.getGameObjects();
				System.arraycopy(interactive, 0, arr, 3, Math.min(len, interactive.length));

				for (final ReflectProxy p : arr) {
					final BasicObject o = new BasicObject(p);
					if (!o.object.isNull()) {
						final int t = o.getMeta() & 0x3f;
						final GameObject.Type type;
						if (t == 0 || t == 1 || t == 9) {
							type = GameObject.Type.BOUNDARY;
						} else if (t == 2 || t == 3 || t == 4 || t == 5 || t == 6 || t == 7 || t == 8) {
							type = GameObject.Type.WALL_DECORATION;
						} else if (t == 10 || t == 11) {
							type = GameObject.Type.INTERACTIVE;
						} else if (t == 22) {
							type = GameObject.Type.FLOOR_DECORATION;
						} else {
							type = GameObject.Type.UNKNOWN;
						}
						set.add(new GameObject(ctx, o, type));
					}
				}
			}
		}
		return new ArrayList<GameObject>(set);
	}

	@Override
	public GameObject nil() {
		return new GameObject(ctx, null, GameObject.Type.UNKNOWN);
	}
}
