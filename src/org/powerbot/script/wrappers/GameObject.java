package org.powerbot.script.wrappers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.lang.ref.WeakReference;

import org.powerbot.bot.AbstractCallback;
import org.powerbot.client.AbstractModel;
import org.powerbot.client.Cache;
import org.powerbot.client.Client;
import org.powerbot.client.HashTable;
import org.powerbot.client.RSAnimable;
import org.powerbot.client.RSInfo;
import org.powerbot.client.RSInteractableData;
import org.powerbot.client.RSInteractableLocation;
import org.powerbot.client.RSObject;
import org.powerbot.client.RSObjectDef;
import org.powerbot.client.RSObjectDefLoader;
import org.powerbot.script.methods.MethodContext;

public class GameObject extends Interactive implements Renderable, Locatable, Nameable, Drawable, Identifiable {
	private static final Color TARGET_COLOR = new Color(0, 255, 0, 20);
	private final WeakReference<RSObject> object;
	private final Type type;
	private int faceIndex = -1;

	public GameObject(MethodContext ctx, final RSObject object, final Type type) {
		super(ctx);
		this.object = new WeakReference<>(object);
		this.type = type;
	}

	public static int clippingTypeForId(int id) {
		Integer type = AbstractCallback.clippingTypes.get(id);
		return type == null ? -1 : type;
	}

	@Override
	public Model getModel() {
		final RSObject object = this.object.get();
		if (object != null) {
			final AbstractModel model = object.getModel();
			if (model != null) {
				return new RenderableModel(ctx, model, object);
			}
		}
		return null;
	}

	@Override
	public int getId() {
		final RSObject object = this.object.get();
		return object != null ? object.getId() : -1;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String getName() {
		return getDefinition().getName();
	}

	public String[] getActions() {
		return getDefinition().getActions();
	}

	public int getPlane() {
		final RSObject object = this.object.get();
		return object != null ? object.getPlane() : -1;
	}

	private ObjectDefinition getDefinition() {
		Client client = ctx.getClient();
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
		final Object def = ctx.game.lookup(table, getId());
		return def != null && def instanceof RSObjectDef ? new ObjectDefinition((RSObjectDef) def) : new ObjectDefinition(null);
	}

	public Area getArea() {
		if (object instanceof RSAnimable) {
			RSAnimable animable = (RSAnimable) object;
			Tile base = ctx.game.getMapBase();
			return new Area(
					base.derive(animable.getX1(), animable.getY1()),
					base.derive(animable.getX2(), animable.getY2())
			);
		}
		Tile loc = getLocation();
		return new Area(loc, loc);
	}

	@Override
	public Tile getLocation() {
		RSObject object = this.object.get();
		RelativeLocation location = getRelative();
		if (object != null && location != null) {
			return ctx.game.getMapBase().derive((int) location.getX() >> 9, (int) location.getY() >> 9, object.getPlane());
		}
		return Tile.NIL;
	}

	public RelativeLocation getRelative() {
		RSObject object = this.object.get();
		RSInteractableData data = object != null ? object.getData() : null;
		RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) {
			return new RelativeLocation(location.getX(), location.getY());
		}
		return RelativeLocation.NIL;
	}

	@Override
	public Point getInteractPoint() {
		final Model model = getModel();
		if (model != null) {
			Point point = model.getCentroid(faceIndex);
			if (point != null) {
				return point;
			}
			point = model.getCentroid(faceIndex = model.nextTriangle());
			if (point != null) {
				return point;
			}
		}
		final Tile tile = getLocation();
		return tile != null ? tile.getMatrix(ctx).getInteractPoint() : new Point(-1, -1);
	}

	@Override
	public Point getNextPoint() {
		final Model model = getModel();
		if (model != null) {
			return model.getNextPoint();
		}
		final Tile tile = getLocation();
		return tile != null ? tile.getMatrix(ctx).getNextPoint() : new Point(-1, -1);
	}

	@Override
	public Point getCenterPoint() {
		final Model model = getModel();
		if (model != null) {
			return model.getCenterPoint();
		}
		final Tile tile = getLocation();
		return tile != null ? tile.getMatrix(ctx).getCenterPoint() : new Point(-1, -1);
	}

	@Override
	public boolean contains(final Point point) {
		final Model model = getModel();
		if (model != null) {
			return model.contains(point);
		}
		final Tile tile = getLocation();
		return tile != null && tile.getMatrix(ctx).contains(point);
	}

	@Override
	public boolean isValid() {
		return this.object.get() != null && ctx.objects.select().contains(this);
	}

	@Override
	public int hashCode() {
		final RSObject i;
		return (i = this.object.get()) != null ? System.identityHashCode(i) : 0;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof GameObject)) {
			return false;
		}
		final GameObject g = (GameObject) o;
		final RSObject i;
		return (i = this.object.get()) != null && i == g.object.get();
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
		final Model m = getModel();
		if (m != null) {
			m.drawWireFrame(render);
		}
	}

	public static enum Type {
		INTERACTIVE, BOUNDARY, WALL_DECORATION, FLOOR_DECORATION, UNKNOWN
	}
}
