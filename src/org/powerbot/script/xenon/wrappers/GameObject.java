package org.powerbot.script.xenon.wrappers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.Arrays;

import org.powerbot.bot.Bot;
import org.powerbot.client.Cache;
import org.powerbot.client.Client;
import org.powerbot.client.HashTable;
import org.powerbot.client.RSInfo;
import org.powerbot.client.RSInteractableData;
import org.powerbot.client.RSInteractableLocation;
import org.powerbot.client.RSObject;
import org.powerbot.client.RSObjectDef;
import org.powerbot.client.RSObjectDefLoader;
import org.powerbot.script.internal.Nodes;
import org.powerbot.script.xenon.Game;
import org.powerbot.script.xenon.Objects;

public class GameObject extends Interactive implements Locatable {
	private static final Color TARGET_COLOR = new Color(0, 255, 0, 20);
	private final WeakReference<RSObject> object;
	private final Type type;
	private int faceIndex = -1;

	public GameObject(final RSObject object, final Type type) {
		this.object = new WeakReference<>(object);
		this.type = type;
	}

	public Model getModel() {
		final RSObject object = this.object.get();
		if (object != null) {
			final org.powerbot.client.Model model = object.getModel();
			if (model != null) return new RenderableModel(model, object);
		}
		return null;
	}

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
		final Client client = Bot.client();
		if (client == null) return null;

		final RSInfo info;
		final RSObjectDefLoader loader;
		final Cache cache;
		final HashTable table;
		if ((info = client.getRSGroundInfo()) == null || (loader = info.getRSObjectDefLoaders()) == null ||
				(cache = loader.getCache()) == null || (table = cache.getTable()) == null) return null;
		final Object def = Nodes.lookup(table, getId());
		return def != null && def instanceof RSObjectDef ? new ObjectDefinition((RSObjectDef) def) : null;
	}

	@Override
	public Tile getLocation() {
		final RSObject object = this.object.get();
		final RSInteractableData data = object != null ? object.getData() : null;
		final RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) {
			final Tile base = Game.getMapBase();
			return base != null ? base.derive((int) location.getX() >> 9, (int) location.getY() >> 9, object.getPlane()) : null;
		}
		return null;
	}

	@Override
	public Point getInteractPoint() {
		final Model model = getModel();
		if (model != null) {
			Point point = model.getCentroid(faceIndex);
			if (point != null) return point;
			point = model.getCentroid(faceIndex = model.nextTriangle());
			if (point != null) return point;
		}
		final Tile tile = getLocation();
		return tile != null ? tile.getInteractPoint() : null;
	}

	@Override
	public Point getNextPoint() {
		final Model model = getModel();
		if (model != null) return model.getNextPoint();
		final Tile tile = getLocation();
		return tile != null ? tile.getNextPoint() : null;
	}

	@Override
	public Point getCenterPoint() {
		final Model model = getModel();
		if (model != null) return model.getCenterPoint();
		final Tile tile = getLocation();
		return tile != null ? tile.getCenterPoint() : null;
	}

	@Override
	public boolean contains(final Point point) {
		final Model model = getModel();
		if (model != null) return model.contains(point);
		final Tile tile = getLocation();
		return tile != null && tile.contains(point);
	}

	@Override
	public boolean isValid() {
		if (this.object.get() == null) return false;
		final Tile tile = getLocation();
		return tile != null && Arrays.asList(Objects.getLoaded(tile.getX(), tile.getY(), 0)).contains(this);
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof GameObject)) return false;
		final GameObject g = (GameObject) o;
		final RSObject i;
		return (i = this.object.get()) != null && i == g.object.get();
	}

	@Override
	public void draw(final Graphics render) {
		render.setColor(TARGET_COLOR);
		final Model m = getModel();
		if (m != null) m.drawWireFrame(render);
	}

	public static enum Type {
		INTERACTIVE, BOUNDARY, WALL_DECORATION, FLOOR_DECORATION
	}
}
