package org.powerbot.script.rs3.tools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.lang.ref.WeakReference;

import org.powerbot.bot.rs3.client.AbstractModel;
import org.powerbot.bot.rs3.client.Cache;
import org.powerbot.bot.rs3.client.Client;
import org.powerbot.bot.rs3.client.HashTable;
import org.powerbot.bot.rs3.client.RSAnimable;
import org.powerbot.bot.rs3.client.RSInfo;
import org.powerbot.bot.rs3.client.RSInteractableData;
import org.powerbot.bot.rs3.client.RSInteractableLocation;
import org.powerbot.bot.rs3.client.RSObject;
import org.powerbot.bot.rs3.client.RSObjectDef;
import org.powerbot.bot.rs3.client.RSObjectDefLoader;

public class GameObject extends Interactive implements Renderable, Locatable, Nameable, Drawable, Identifiable {
	private static final Color TARGET_COLOR = new Color(0, 255, 0, 20);
	private final WeakReference<RSObject> object;
	private final Type type;
	private int faceIndex = -1;

	public GameObject(final ClientContext ctx, final RSObject object, final Type type) {
		super(ctx);
		this.object = new WeakReference<RSObject>(object);
		this.type = type;
	}

	@Override
	public void setBounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) {
		boundingModel.set(new BoundingModel(ctx, x1, x2, y1, y2, z1, z2) {
			@Override
			public int getX() {
				final RelativeLocation r = getRelative();
				return (int) r.getX();
			}

			@Override
			public int getZ() {
				final RelativeLocation r = getRelative();
				return (int) r.getY();
			}
		});
	}

	@Override
	public Model getModel() {
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

	public RSObject getInternal() {
		return object.get();
	}

	private ObjectDefinition getDefinition() {
		final Client client = ctx.getClient();
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
			final RSAnimable animable = (RSAnimable) object;
			final Tile base = ctx.game.getMapBase();
			return new Area(
					base.derive(animable.getX1(), animable.getY1()),
					base.derive(animable.getX2(), animable.getY2())
			);
		}
		final Tile loc = getLocation();
		return new Area(loc, loc);
	}

	@Override
	public Tile getLocation() {
		final RSObject object = this.object.get();
		final RelativeLocation location = getRelative();
		if (object != null && location != null) {
			return ctx.game.getMapBase().derive((int) location.getX() >> 9, (int) location.getY() >> 9, object.getPlane());
		}
		return Tile.NIL;
	}

	public RelativeLocation getRelative() {
		final RSObject object = this.object.get();
		final RSInteractableData data = object != null ? object.getData() : null;
		final RSInteractableLocation location = data != null ? data.getLocation() : null;
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
		final BoundingModel model2 = boundingModel.get();
		if (model2 != null) {
			return model2.getNextPoint();
		}
		return getLocation().getMatrix(ctx).getInteractPoint();
	}

	@Override
	public Point getNextPoint() {
		final Model model = getModel();
		if (model != null) {
			return model.getNextPoint();
		}
		final BoundingModel model2 = boundingModel.get();
		if (model2 != null) {
			return model2.getNextPoint();
		}
		return getLocation().getMatrix(ctx).getNextPoint();
	}

	@Override
	public Point getCenterPoint() {
		final Model model = getModel();
		if (model != null) {
			return model.getCenterPoint();
		}
		final BoundingModel model2 = boundingModel.get();
		if (model2 != null) {
			return model2.getCenterPoint();
		}
		return getLocation().getMatrix(ctx).getCenterPoint();
	}

	@Override
	public boolean contains(final Point point) {
		final Model model = getModel();
		if (model != null) {
			return model.contains(point);
		}
		final BoundingModel model2 = boundingModel.get();
		if (model2 != null) {
			return model2.contains(point);
		}
		return getLocation().getMatrix(ctx).contains(point);
	}

	@Override
	public boolean isValid() {
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
			final Model m = getModel();
			if (m != null) {
				m.drawWireFrame(render);
			}
		}
	}

	@Override
	public String toString() {
		return GameObject.class.getSimpleName() + "[id=" + getId() + ",name=" + getName() + "]";
	}

	public static enum Type {
		INTERACTIVE, BOUNDARY, WALL_DECORATION, FLOOR_DECORATION, UNKNOWN
	}
}
