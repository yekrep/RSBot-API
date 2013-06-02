package org.powerbot.script.methods;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.powerbot.bot.World;
import org.powerbot.client.BaseInfo;
import org.powerbot.client.Client;
import org.powerbot.client.RSAnimableNode;
import org.powerbot.client.RSGround;
import org.powerbot.client.RSGroundInfo;
import org.powerbot.client.RSInfo;
import org.powerbot.client.RSObject;
import org.powerbot.script.util.Filter;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;

public class Objects {
	private static final int LOADED_DIST = 104;

	public static GameObject[] getLoaded() {
		return getLoaded(LOADED_DIST);
	}

	public static GameObject[] getLoaded(int _x, int _y, final int range) {
		final Set<GameObject> objects = new LinkedHashSet<>();
		final Client client = World.getWorld().getClient();
		if (client == null) return new GameObject[0];

		final RSInfo info;
		final BaseInfo baseInfo;
		final RSGroundInfo groundInfo;
		final RSGround[][][] grounds;
		if ((info = client.getRSGroundInfo()) == null || (baseInfo = info.getBaseInfo()) == null ||
				(groundInfo = info.getRSGroundInfo()) == null || (grounds = groundInfo.getRSGroundArray()) == null)
			return new GameObject[0];

		final GameObject.Type[] types = {
				GameObject.Type.BOUNDARY, GameObject.Type.BOUNDARY,
				GameObject.Type.FLOOR_DECORATION,
				GameObject.Type.WALL_DECORATION, GameObject.Type.WALL_DECORATION
		};

		_x -= baseInfo.getX();
		_y -= baseInfo.getY();
		final int plane = client.getPlane();

		final RSGround[][] objArr = plane > -1 && plane < grounds.length ? grounds[plane] : null;
		if (objArr == null) return new GameObject[0];
		for (int x = Math.max(0, _x - range); x <= Math.min(_x + range, objArr.length - 1); x++) {
			for (int y = Math.max(0, _y - range); y <= Math.min(_y + range, objArr[x].length - 1); y++) {
				final RSGround ground = objArr[x][y];
				if (ground == null) continue;

				for (RSAnimableNode node = ground.getRSAnimableList(); node != null; node = node.getNext()) {
					final RSObject obj = node.getRSAnimable();
					if (obj != null && obj.getId() != -1) objects.add(new GameObject(obj, GameObject.Type.INTERACTIVE));
				}


				final RSObject[] objs = {
						ground.getBoundary1(), ground.getBoundary2(),
						ground.getFloorDecoration(),
						ground.getWallDecoration1(), ground.getWallDecoration2()
				};

				for (int i = 0; i < objs.length; i++) {
					if (objs[i] != null && objs[i].getId() != -1) objects.add(new GameObject(objs[i], types[i]));
				}
			}
		}
		return objects.toArray(new GameObject[objects.size()]);
	}

	public static GameObject[] getLoaded(final int range) {
		final Player player = Players.getLocal();
		final Tile location;
		if (player == null || (location = player.getLocation()) == null) {
			return new GameObject[0];
		}

		final int x = location.getX(), y = location.getY();
		return getLoaded(x, y, range);
	}

	public static GameObject[] getLoaded(final Filter<GameObject> filter) {
		return getLoaded(LOADED_DIST, filter);
	}

	public static GameObject[] getLoaded(final int range, final Filter<GameObject> filter) {
		final GameObject[] items = getLoaded(range);
		final GameObject[] set = new GameObject[items.length];
		int d = 0;
		for (final GameObject item : items) if (filter.accept(item)) set[d++] = item;
		return Arrays.copyOf(set, d);
	}

	public static GameObject[] getLoaded(final int... ids) {
		return getLoaded(LOADED_DIST, ids);
	}

	public static GameObject[] getLoaded(final int range, final int[] ids) {
		return getLoaded(range, new Filter<GameObject>() {
			@Override
			public boolean accept(final GameObject gameObject) {
				final int objectId = gameObject.getId();
				for (final int id : ids) if (objectId == id) return true;
				return false;
			}
		});
	}

	public static GameObject getNearest(final Filter<GameObject> filter) {
		return getNearest(LOADED_DIST, filter);
	}

	public static GameObject getNearest(final int range, final Filter<GameObject> filter) {
		GameObject nearest = null;
		double dist = 104d;

		final Player local = Players.getLocal();
		if (local == null) return null;

		final Tile pos = local.getLocation();
		if (pos == null) return null;
		final GameObject[] gameObjects = getLoaded(range);
		for (final GameObject gameObject : gameObjects) {
			final double d;
			if (filter.accept(gameObject) && (d = Movement.distance(pos, gameObject)) < dist) {
				nearest = gameObject;
				dist = d;
			}
		}

		return nearest;
	}

	public static GameObject getNearest(final int range, final int[] ids) {
		return getNearest(range, new Filter<GameObject>() {
			@Override
			public boolean accept(final GameObject gameObject) {
				final int objectId = gameObject.getId();
				for (final int id : ids) if (objectId == id) return true;
				return false;
			}
		});
	}

	public static GameObject getNearest(final int... ids) {
		return getNearest(LOADED_DIST, ids);
	}
}
