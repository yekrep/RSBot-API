package org.powerbot.script.rt6;

import java.awt.Color;
import java.awt.Point;

import org.powerbot.bot.rt6.client.RelativePosition;
import org.powerbot.script.Actionable;
import org.powerbot.script.Area;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Locatable;
import org.powerbot.script.Nameable;
import org.powerbot.script.Tile;

public class GameObject extends Interactive implements Locatable, Nameable, Identifiable, Actionable {
	private static final Color TARGET_COLOR = new Color(0, 255, 0, 20);
	public final BasicObject object;
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
		return object != null ? object.getId() : -1;
	}

	public Type type() {
		return type;
	}

	public int animation() {
		return object != null ? object.getAnimator().getSequence().getId() : -1;
	}

	@Override
	public String name() {
		return "";//TODO: this
	}

	@Override
	public String[] actions() {
		return new String[0];//TODO: this
	}

	public int orientation() {
		return object != null ? object.getOrientation() : -1;
	}

	public int floor() {
		return object != null ? object.getFloor() : -1;
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
		if (object == null || object.object.isNull()) {
			return Tile.NIL;
		}
		return ctx.game.mapOffset().derive((int) location.x() >> 9, (int) location.z() >> 9, object.getFloor());
	}

	public RelativeLocation relative() {
		if (object == null) {
			return RelativeLocation.NIL;
		}
		final RelativePosition location = object.object.getLocation().getRelativePosition();
		if (location.isNull()) {
			return RelativeLocation.NIL;
		}
		return new RelativeLocation(location.getX(), location.getZ());
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
		return object != null && object.getObject() != null && ctx.objects.select().contains(this);
	}

	@Override
	public int hashCode() {
		return object != null ? object.hashCode() : 0;
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof GameObject && object != null && object.equals(((GameObject) o).object);
	}

	@Override
	public String toString() {
		return GameObject.class.getSimpleName() + "[id=" + id() + ",name=" + name() + "]";
	}

	public enum Type {
		INTERACTIVE, BOUNDARY, WALL_DECORATION, FLOOR_DECORATION, UNKNOWN
	}
}
