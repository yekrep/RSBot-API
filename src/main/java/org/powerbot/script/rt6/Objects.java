package org.powerbot.script.rt6;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;
import org.powerbot.bot.rt6.client.BoundaryObject;
import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.DynamicBoundaryObject;
import org.powerbot.bot.rt6.client.DynamicFloorObject;
import org.powerbot.bot.rt6.client.DynamicGameObject;
import org.powerbot.bot.rt6.client.DynamicWallObject;
import org.powerbot.bot.rt6.client.FloorObject;
import org.powerbot.bot.rt6.client.RenderableEntity;
import org.powerbot.bot.rt6.client.RenderableNode;
import org.powerbot.bot.rt6.client.Tile;
import org.powerbot.bot.rt6.client.WallObject;
import org.powerbot.script.Locatable;

/**
 * Objects
 * Utilities pertaining to in-game objects.
 */
public class Objects extends MobileIdNameQuery<GameObject> {
	private GameObject NIL;

	private static final Class<?> o_types[][] = {
			{BoundaryObject.class, DynamicBoundaryObject.class}, {BoundaryObject.class, DynamicBoundaryObject.class},
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
		NIL = new GameObject(ctx, null, GameObject.Type.UNKNOWN);
	}

	public MobileIdNameQuery<GameObject> select(final int radius) {
		return select(get(radius));
	}

	public MobileIdNameQuery<GameObject> select(final Locatable l, final int radius) {
		return select(get(l, radius));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<GameObject> get() {
		return get(Integer.MAX_VALUE);
	}

	protected List<GameObject> get(final int radius) {
		return get(ctx.players.local(), radius);
	}

	protected List<GameObject> get(final Locatable l, int radius) {
		radius = Math.min(radius, 110);
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
		final Tile[][] rows = grounds[floor];
		int start_x = 0, end_x = Integer.MAX_VALUE, start_y = 0, end_y = Integer.MAX_VALUE;
		if (radius >= 0) {
			final org.powerbot.script.Tile mo = ctx.game.mapOffset(), lp = l.tile();
			if (mo != org.powerbot.script.Tile.NIL && lp != org.powerbot.script.Tile.NIL) {
				final org.powerbot.script.Tile t = lp.derive(-mo.x(), -mo.y());
				start_x = t.x() - radius;
				end_x = t.x() + radius;
				start_y = t.y() - radius;
				end_y = t.y() + radius;
			}
		}
		for (int x = Math.max(0, start_x); x <= Math.min(end_x, rows.length - 1); x++) {
			final Tile[] col = rows[x];
			for (int y = Math.max(0, start_y); y <= Math.min(end_y, col.length - 1); y++) {
				final Tile tile = col[y];
				if (tile.isNull()) {
					continue;
				}
				for (RenderableNode node = tile.getInteractives(); !node.isNull(); node = node.getNext()) {
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
						tile.getBoundary1(), tile.getBoundary2(),
						tile.getFloorDecoration(),
						tile.getWallDecoration1(), tile.getWallDecoration2()
				};
				for (int i = 0; i < objs.length; i++) {
					if (objs[i] == null) {
						continue;
					}
					Class<?> type = null;
					for (final Class<?> e : o_types[i]) {
						@SuppressWarnings("unchecked")
						final Class<? extends ReflectProxy> c = (Class<? extends ReflectProxy>) e;
						if (c != null && tile.reflector.isTypeOf(objs[i], c)) {
							type = c;
							break;
						}
					}
					if (type == null) {
						continue;
					}
					try {
						items.add(new GameObject(ctx,
								new BasicObject((RenderableEntity) type.getConstructor(Reflector.class, Object.class).newInstance(tile.reflector, objs[i]), floor),
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
		return NIL;
	}
}
