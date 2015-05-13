package org.powerbot.script.rt6;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;
import org.powerbot.bot.rt4.client.FloorObject;
import org.powerbot.bot.rt6.client.BoundaryObject;
import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.DynamicFloorObject;
import org.powerbot.bot.rt6.client.DynamicGameObject;
import org.powerbot.bot.rt6.client.DynamicWallObject;
import org.powerbot.bot.rt6.client.RenderableEntity;
import org.powerbot.bot.rt6.client.RenderableNode;
import org.powerbot.bot.rt6.client.Tile;
import org.powerbot.bot.rt6.client.WallObject;

/**
 * Utilities pertaining to in-game objects.
 */
public class Objects extends MobileIdNameQuery<GameObject> {
	public final Map<Integer, Integer> typeCache = new ConcurrentHashMap<Integer, Integer>();
	private static final Class<?> o_types[][] = {
			{BoundaryObject.class, null}, {BoundaryObject.class, null},
			{FloorObject.class, DynamicFloorObject.class},
			{WallObject.class, DynamicWallObject.class}, {WallObject.class, DynamicWallObject.class}
	};
	private static final GameObject.Type[] types = {
			GameObject.Type.BOUNDARY, GameObject.Type.BOUNDARY,
			GameObject.Type.FLOOR_DECORATION,
			GameObject.Type.WALL_DECORATION, GameObject.Type.WALL_DECORATION
	};

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
		final Tile[][][] grounds = client.getWorld().getLandscape().getTiles();
		final int floor = ctx.game.floor();
		if (floor < 0 || floor >= grounds.length) {
			return items;
		}
		final Set<GameObject> set = new HashSet<GameObject>();
		final Tile[][] map = grounds[floor];
		for (int x = 0; x < map.length; x++) {
			for (int y = 0; y < map[x].length; y++) {
				final Tile g = map[x][y];
				if (g.isNull()) {
					continue;
				}
				for (RenderableNode node = g.getInteractives(); !node.isNull(); node = node.getNext()) {
					final RenderableEntity r = node.getEntity();
					if (r.isNull()) {
						continue;
					}
					if (r.isTypeOf(org.powerbot.bot.rt6.client.GameObject.class)) {
						final org.powerbot.bot.rt6.client.GameObject o = new org.powerbot.bot.rt6.client.GameObject(r.reflector, r);
						if (o.getId() != -1) {
							set.add(new GameObject(ctx, new BasicObject(o, floor), GameObject.Type.INTERACTIVE));
						}
					} else if (r.isTypeOf(DynamicGameObject.class)) {
						final DynamicGameObject o = new DynamicGameObject(r.reflector, r);
						if (o.getBridge().getId() != -1) {
							set.add(new GameObject(ctx, new BasicObject(o, floor), GameObject.Type.INTERACTIVE));
						}
					}
				}
				final Object[] objs = {
						g.getBoundary1(), g.getBoundary2(),
						g.getFloorDecoration(),
						g.getWallDecoration1(), g.getWallDecoration2()
				};
				for (int i = 0; i < objs.length; i++) {
					if (objs[i] == null) {
						continue;
					}
					Class<?> type = null;
					for (final Class<?> e : o_types[i]) {
						@SuppressWarnings("unchecked")
						final Class<? extends ReflectProxy> c = (Class<? extends ReflectProxy>) e;
						if (c != null && g.reflector.isTypeOf(objs[i], c)) {
							type = c;
							break;
						}
					}
					if (type == null) {
						continue;
					}
					try {
						items.add(new GameObject(ctx,
								new BasicObject((RenderableEntity) type.getConstructor(Reflector.class, Object.class).newInstance(g.reflector, objs[i]), floor),
								types[i]));
					} catch (final InstantiationException ignored) {
					} catch (final IllegalAccessException ignored) {
					} catch (final InvocationTargetException ignored) {
					} catch (final NoSuchMethodException ignored) {
					}
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
