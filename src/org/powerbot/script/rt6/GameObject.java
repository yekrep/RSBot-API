package org.powerbot.script.rt6;

import java.awt.Color;
import java.awt.Point;

import org.powerbot.bot.rt6.client.BoundaryObject;
import org.powerbot.bot.rt6.client.GameLocation;
import org.powerbot.bot.rt6.client.RelativePosition;
import org.powerbot.script.Area;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Locatable;
import org.powerbot.script.Nameable;
import org.powerbot.script.Tile;

public class GameObject extends Interactive implements Locatable, Nameable, Identifiable {
	private static final Color TARGET_COLOR = new Color(0, 255, 0, 20);
	private final BasicObject object;
	private final Type type;

	public GameObject(final ClientContext ctx, final BasicObject object, final Type type) {
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
		return "";//TODO: this
	}

	public int orientation() {
		if (type != Type.BOUNDARY || ctx.objects.type(id()) == 0 || object == null ||
				!object.isTypeOf(BoundaryObject.class)) {
			return -1;
		}
		return new BoundaryObject(object.reflector, object).getOrientation();
	}

	public int floor() {
		return object.getFloor();
	}

	//TODO: get config

	public Area area() {
		//TODO: special type
		final Tile loc = tile();
		return new Area(loc, loc);
	}

	@Override
	public Tile tile() {
		final RelativeLocation location = relative();
		if (object.getObject() != null && location != null) {
			return ctx.game.mapOffset().derive((int) location.x() >> 9, (int) location.z() >> 9, object.getFloor());
		}
		return Tile.NIL;
	}

	public RelativeLocation relative() {
		final GameLocation data = object.getLocation();
		final RelativePosition location = data != null ? data.getRelativePosition() : null;
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
		return object.getObject() != null && ctx.objects.select().contains(this);
	}

	@Override
	public int hashCode() {
		return object.hashCode();
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof GameObject && object.equals(((GameObject) o).object);
	}

	@Override
	public String toString() {
		return GameObject.class.getSimpleName() + "[id=" + id() + ",name=" + name() + "]";
	}

	public static enum Type {
		INTERACTIVE, BOUNDARY, WALL_DECORATION, FLOOR_DECORATION, UNKNOWN
	}
}
