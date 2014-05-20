package org.powerbot.script.rt4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.powerbot.bot.rt4.client.BoundaryObject;
import org.powerbot.bot.rt4.client.Client;
import org.powerbot.bot.rt4.client.FloorObject;
import org.powerbot.bot.rt4.client.Landscape;
import org.powerbot.bot.rt4.client.Tile;
import org.powerbot.bot.rt4.client.WallObject;

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
			System.out.println(Arrays.toString(row));
			for (final Tile tile : row) {
				if (tile.obj.get() == null) {
					continue;
				}
				final BoundaryObject bo = tile.getBoundaryObject();
				final FloorObject fo = tile.getFloorObject();
				final WallObject wo = tile.getWallObject();
				final int len = Math.max(0, tile.getGameObjectLength());
				final org.powerbot.bot.rt4.client.GameObject[] arr = new org.powerbot.bot.rt4.client.GameObject[len];
				final org.powerbot.bot.rt4.client.GameObject[] interactive = tile.getGameObjects();
				if (interactive != null) {
					System.arraycopy(interactive, 0, arr, 0, Math.min(len, interactive.length));
				}
				final BasicObject[] arr2 = new BasicObject[3 + len];
				arr2[0] = new BasicObject(bo);
				arr2[1] = new BasicObject(fo);
				arr2[2] = new BasicObject(wo);
				for (int i = 3; i < len; i++) {
					arr2[i] = new ComplexObject(arr[i - 3]);
				}

				for (final BasicObject o : arr2) {
					if (o.getObject() != null) {
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
