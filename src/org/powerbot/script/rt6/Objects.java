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
import org.powerbot.bot.rt6.client.RSGroundInfo;
import org.powerbot.bot.rt6.client.RSInfo;
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

		final RSInfo info;
		final RSGroundInfo groundInfo;
		final RSGround[][][] grounds;
		if ((info = client.getRSGroundInfo()) == null || (groundInfo = info.getRSGroundInfo()) == null ||
				(grounds = groundInfo.getRSGroundArray()) == null) {
			return items;
		}

		final GameObject.Type[] types = {
				GameObject.Type.BOUNDARY, GameObject.Type.BOUNDARY,
				GameObject.Type.FLOOR_DECORATION,
				GameObject.Type.WALL_DECORATION, GameObject.Type.WALL_DECORATION
		};

		final int plane = client.getPlane();

		final RSGround[][] objArr = plane > -1 && plane < grounds.length ? grounds[plane] : null;
		if (objArr == null) {
			return items;
		}

		final Set<RSObject> refs = new HashSet<RSObject>();
		for (int x = 0; x <= objArr.length - 1; x++) {
			for (int y = 0; y <= objArr[x].length - 1; y++) {
				final RSGround ground = objArr[x][y];
				if (ground == null) {
					continue;
				}

				for (RSAnimableNode animable = ground.getRSAnimableList(); animable != null; animable = animable.getNext()) {
					final Object node = animable.getRSAnimable();
					if (node == null) {
						continue;
					}
					final RSObject obj = (RSObject) node;
					if (obj.getId() != -1 && !refs.contains(obj)) {
						refs.add(obj);
						items.add(new GameObject(ctx, obj, GameObject.Type.INTERACTIVE));
					}
				}


				final RSObject[] objs = {
						ground.getBoundary1(), ground.getBoundary2(),
						ground.getFloorDecoration(),
						ground.getWallDecoration1(), ground.getWallDecoration2()
				};

				for (int i = 0; i < objs.length; i++) {
					if (objs[i] != null && objs[i].getId() != -1) {
						items.add(new GameObject(ctx, objs[i], types[i]));
					}
				}
			}
		}
		refs.clear();//help gc
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
