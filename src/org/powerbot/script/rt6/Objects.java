package org.powerbot.script.rt6;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.RSAnimableNode;
import org.powerbot.bot.rt6.client.RSGround;
import org.powerbot.bot.rt6.client.RSObject;

/**
 * Utilities pertaining to in-game objects.
 */
public class Objects extends MobileIdNameQuery<GameObject> {
	public final Map<Integer, Integer> typeCache = new ConcurrentHashMap<Integer, Integer>();

	public Objects(final ClientContext factory) {
		super(factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<GameObject> get() {
		final List<GameObject> items = new ArrayList<GameObject>();
		final Client client = ctx.client();
		if (client == null) {
			return items;
		}
		final RSGround[][][] grounds = client.getRSGroundInfo().getRSGroundInfo().getRSGroundArray();
		final int floor = ctx.game.floor();
		if (floor < 0 || floor >= grounds.length) {
			return items;
		}

		final GameObject.Type[] types = {
				GameObject.Type.BOUNDARY, GameObject.Type.BOUNDARY,
				GameObject.Type.FLOOR_DECORATION,
				GameObject.Type.WALL_DECORATION, GameObject.Type.WALL_DECORATION
		};
		final Set<GameObject> set = new HashSet<GameObject>();
		final RSGround[][] map = grounds[floor];
		for (int x = 0; x < map.length; x++) {
			for (int y = 0; y < map[x].length; y++) {
				final RSGround g = map[x][y];
				if (g.isNull()) {
					continue;
				}
				for (RSAnimableNode node = g.getRSAnimableList(); !node.isNull(); node = node.getNext()) {
					final RSObject r = node.getRSAnimable();
					if (r.isNull()) {
						continue;
					}

					if (r.getId() != -1) {
						set.add(new GameObject(ctx, r, GameObject.Type.INTERACTIVE));
					}
				}

				final RSObject[] objs = {
						g.getBoundary1(), g.getBoundary2(),
						g.getFloorDecoration(),
						g.getWallDecoration1(), g.getWallDecoration2()
				};
				for (int i = 0; i < objs.length; i++) {
					if (objs[i].isNull() || objs[i].getId() == -1) {
						continue;
					}
					items.add(new GameObject(ctx, objs[i], types[i]));
				}
			}
		}
		items.addAll(set);
		set.clear();
		return items;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GameObject nil() {
		return new GameObject(ctx, null, GameObject.Type.UNKNOWN);
	}

	public void mapType(final int id, final int type) {
		typeCache.put(id, type);
	}

	public int type(final int id) {
		final Integer integer = typeCache.get(id);
		return integer != null ? integer : -1;
	}
}
