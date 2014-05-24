package org.powerbot.script.rt6;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.bot.rt6.client.Cache;
import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.HashTable;
import org.powerbot.bot.rt6.client.RSInfo;
import org.powerbot.bot.rt6.client.RSInteractableData;
import org.powerbot.bot.rt6.client.RSInteractableLocation;
import org.powerbot.bot.rt6.client.RSObject;
import org.powerbot.bot.rt6.client.RSObjectDef;
import org.powerbot.bot.rt6.client.RSObjectDefLoader;
import org.powerbot.script.Area;
import org.powerbot.script.Drawable;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Locatable;
import org.powerbot.script.Nameable;
import org.powerbot.script.Tile;

public class GameObject extends Interactive implements Locatable, Nameable, Identifiable {
	private static final Color TARGET_COLOR = new Color(0, 255, 0, 20);
	private final RSObject object;
	private final Type type;

	public GameObject(final ClientContext ctx, final RSObject object, final Type type) {
		super(ctx);
		this.object = object;
		this.type = type;
		bounds(-128, 128, -256, 0, -128, 128);
	}

	@Override
	public void bounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) {
		boundingModel.set(new BoundingModel(ctx, x1, x2, y1, y2, z1, z2) {
			@Override
			public int x() {
				final RelativeLocation r = relative();
				return (int) r.x();
			}

			@Override
			public int z() {
				final RelativeLocation r = relative();
				return (int) r.z();
			}
		});
	}

	@Override
	public int id() {
		return object.getId();
	}

	public Type type() {
		return type;
	}

	@Override
	public String name() {
		return getConfig().getName();
	}

	public String[] actions() {
		return getConfig().getActions();
	}

	public int floor() {
		return object.getPlane();
	}

	private ObjectConfig getConfig() {
		final Client client = ctx.client();
		if (client == null) {
			return new ObjectConfig(null);
		}

		final RSInfo info;
		final RSObjectDefLoader loader;
		final Cache cache;
		final HashTable table;
		if ((info = client.getRSGroundInfo()) == null || (loader = info.getRSObjectDefLoaders()) == null ||
				(cache = loader.getCache()) == null || (table = cache.getTable()) == null) {
			return new ObjectConfig(new RSObjectDef(client.reflector, null));
		}
		final Object def = org.powerbot.bot.rt6.tools.HashTable.lookup(table, id());
		return new ObjectConfig(new RSObjectDef(client.reflector, def));
	}

	public Area area() {
		//TODO: special type
		final Tile loc = tile();
		return new Area(loc, loc);
	}

	@Override
	public Tile tile() {
		final RelativeLocation location = relative();
		if (object.obj.get() != null && location != null) {
			return ctx.game.mapOffset().derive((int) location.x() >> 9, (int) location.z() >> 9, object.getPlane());
		}
		return Tile.NIL;
	}

	public RelativeLocation relative() {
		final RSInteractableData data = object.getData();
		final RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) {
			return new RelativeLocation(location.getX(), location.getY());
		}
		return RelativeLocation.NIL;
	}

	@Override
	public Point nextPoint() {
		final BoundingModel model = boundingModel.get();
		return model != null ? model.nextPoint() : new Point(-1, -1);
	}

	public Point centerPoint() {
		final BoundingModel model = boundingModel.get();
		return model != null ? model.centerPoint() : new Point(-1, -1);
	}

	@Override
	public boolean contains(final Point point) {
		final BoundingModel model = boundingModel.get();
		return model != null && model.contains(point);
	}

	@Override
	public boolean valid() {
		return object.obj.get() != null && ctx.objects.select().contains(this);
	}

	@Override
	public int hashCode() {
		return object.hashCode();
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof GameObject && object != null && object.equals(((GameObject) o).object);
	}

	@Override
	public String toString() {
		return GameObject.class.getSimpleName() + "[id=" + id() + ",name=" + name() + "]";
	}

	public RSObject internal() {
		return object;
	}

	public static enum Type {
		INTERACTIVE, BOUNDARY, WALL_DECORATION, FLOOR_DECORATION, UNKNOWN
	}
}
