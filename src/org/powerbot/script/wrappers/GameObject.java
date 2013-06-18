package org.powerbot.script.wrappers;

import org.powerbot.client.Cache;
import org.powerbot.client.Client;
import org.powerbot.client.HashTable;
import org.powerbot.client.RSInfo;
import org.powerbot.client.RSInteractableData;
import org.powerbot.client.RSInteractableLocation;
import org.powerbot.client.RSObject;
import org.powerbot.client.RSObjectDef;
import org.powerbot.client.RSObjectDefLoader;
import org.powerbot.script.lang.Drawable;
import org.powerbot.script.lang.Identifiable;
import org.powerbot.script.lang.Locatable;
import org.powerbot.script.methods.MethodContext;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.lang.ref.WeakReference;

public class GameObject extends Interactive implements Locatable, Drawable, Identifiable {
	private static final Color TARGET_COLOR = new Color(0, 255, 0, 20);
	private final WeakReference<RSObject> object;
	private final Type type;
	private int faceIndex = -1;

	public GameObject(MethodContext ctx, final RSObject object, final Type type) {
		super(ctx);
		this.object = new WeakReference<>(object);
		this.type = type;
	}

	public Model getModel() {
		final RSObject object = this.object.get();
		if (object != null) {
			final org.powerbot.client.Model model = object.getModel();
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

	public int getPlane() {
		final RSObject object = this.object.get();
		return object != null ? object.getPlane() : -1;
	}

	public ObjectDefinition getDefinition() {
		Client client = ctx.getClient();
		if (client == null) {
			return null;
		}

		final RSInfo info;
		final RSObjectDefLoader loader;
		final Cache cache;
		final HashTable table;
		if ((info = client.getRSGroundInfo()) == null || (loader = info.getRSObjectDefLoaders()) == null ||
				(cache = loader.getCache()) == null || (table = cache.getTable()) == null) {
			return null;
		}
		final Object def = ctx.game.lookup(table, getId());
		return def != null && def instanceof RSObjectDef ? new ObjectDefinition((RSObjectDef) def) : new ObjectDefinition(null);
	}

	@Override
	public Tile getLocation() {
		final RSObject object = this.object.get();
		final RSInteractableData data = object != null ? object.getData() : null;
		final RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) {
			return ctx.game.getMapBase().derive((int) location.getX() >> 9, (int) location.getY() >> 9, object.getPlane());
		}
		return new Tile(-1, -1, -1);
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
		INTERACTIVE, BOUNDARY, WALL_DECORATION, FLOOR_DECORATION
	}
}
