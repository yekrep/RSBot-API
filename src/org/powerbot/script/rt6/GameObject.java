package org.powerbot.script.rt6;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.lang.ref.WeakReference;

import org.powerbot.bot.rt6.client.AbstractModel;
import org.powerbot.bot.rt6.client.Cache;
import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.HashTable;
import org.powerbot.bot.rt6.client.RSAnimable;
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

public class GameObject extends Interactive implements Renderable, Locatable, Nameable, Drawable, Identifiable {
	private static final Color TARGET_COLOR = new Color(0, 255, 0, 20);
	private final WeakReference<RSObject> object;
	private final Type type;

	public GameObject(final ClientContext ctx, final RSObject object, final Type type) {
		super(ctx);
		this.object = new WeakReference<RSObject>(object);
		this.type = type;
		bounds(-256, 256, -512, 0, -256, 256);
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
	public Model model() {
		final RSObject object = this.object.get();
		if (object != null && ctx.game.toolkit.gameMode == 0) {
			final AbstractModel model = object.getModel();
			if (model != null) {
				return new RenderableModel(ctx, model, object);
			}
		}
		return null;
	}

	@Override
	public int id() {
		final RSObject object = this.object.get();
		return object != null ? object.getId() : -1;
	}

	public Type type() {
		return type;
	}

	@Override
	public String name() {
		return getDefinition().getName();
	}

	public String[] actions() {
		return getDefinition().getActions();
	}

	public int floor() {
		final RSObject object = this.object.get();
		return object != null ? object.getPlane() : -1;
	}

	public RSObject internal() {
		return object.get();
	}

	private ObjectDefinition getDefinition() {
		final Client client = ctx.client();
		if (client == null) {
			return new ObjectDefinition(null);
		}

		final RSInfo info;
		final RSObjectDefLoader loader;
		final Cache cache;
		final HashTable table;
		if ((info = client.getRSGroundInfo()) == null || (loader = info.getRSObjectDefLoaders()) == null ||
				(cache = loader.getCache()) == null || (table = cache.getTable()) == null) {
			return new ObjectDefinition(null);
		}
		final Object def = ctx.game.lookup(table, id());
		return def != null && def instanceof RSObjectDef ? new ObjectDefinition((RSObjectDef) def) : new ObjectDefinition(null);
	}

	public Area area() {
		if (object instanceof RSAnimable) {
			final RSAnimable animable = (RSAnimable) object;
			final Tile base = ctx.game.mapOffset();
			return new Area(
					base.derive(animable.getX1(), animable.getY1()),
					base.derive(animable.getX2(), animable.getY2())
			);
		}
		final Tile loc = tile();
		return new Area(loc, loc);
	}

	@Override
	public Tile tile() {
		final RSObject object = this.object.get();
		final RelativeLocation location = relative();
		if (object != null && location != null) {
			return ctx.game.mapOffset().derive((int) location.x() >> 9, (int) location.z() >> 9, object.getPlane());
		}
		return Tile.NIL;
	}

	public RelativeLocation relative() {
		final RSObject object = this.object.get();
		final RSInteractableData data = object != null ? object.getData() : null;
		final RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) {
			return new RelativeLocation(location.getX(), location.getY());
		}
		return RelativeLocation.NIL;
	}

	@Override
	public Point nextPoint() {
		final Model model = model();
		if (model != null) {
			return model.nextPoint();
		}
		final BoundingModel model2 = boundingModel.get();
		if (model2 != null) {
			return model2.nextPoint();
		}
		return new TileMatrix(ctx, tile()).nextPoint();
	}

	public Point centerPoint() {
		final Model model = model();
		if (model != null) {
			return model.centerPoint();
		}
		final BoundingModel model2 = boundingModel.get();
		if (model2 != null) {
			return model2.centerPoint();
		}
		return new TileMatrix(ctx, tile()).centerPoint();
	}

	@Override
	public boolean contains(final Point point) {
		final Model model = model();
		if (model != null) {
			return model.contains(point);
		}
		final BoundingModel model2 = boundingModel.get();
		if (model2 != null) {
			return model2.contains(point);
		}
		return new TileMatrix(ctx, tile()).contains(point);
	}

	@Override
	public boolean valid() {
		return object.get() != null && ctx.objects.select().contains(this);
	}

	@Override
	public int hashCode() {
		final RSObject i;
		return (i = object.get()) != null ? System.identityHashCode(i) : 0;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof GameObject)) {
			return false;
		}
		final GameObject g = (GameObject) o;
		final RSObject i;
		return (i = object.get()) != null && i == g.object.get();
	}

	@Override
	public void draw(final Graphics render) {
		draw(render, 20);
	}

	@Override
	public void draw(final Graphics render, final int alpha) {
		Color c = TARGET_COLOR;
		final int rgb = c.getRGB();
		if (((rgb >> 24) & 0xff) != alpha) {
			c = new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff, alpha);
		}
		render.setColor(c);
		final BoundingModel m2 = boundingModel.get();
		if (m2 != null) {
			m2.drawWireFrame(render);
		} else {
			final Model m = model();
			if (m != null) {
				m.drawWireFrame(render);
			}
		}
	}

	@Override
	public String toString() {
		return GameObject.class.getSimpleName() + "[id=" + id() + ",name=" + name() + "]";
	}

	public static enum Type {
		INTERACTIVE, BOUNDARY, WALL_DECORATION, FLOOR_DECORATION, UNKNOWN
	}
}
