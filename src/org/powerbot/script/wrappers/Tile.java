package org.powerbot.script.wrappers;

import org.powerbot.script.lang.Locatable;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Random;

import java.awt.Color;

/**
 * Represents a position in three-dimensional game space.
 */
public class Tile implements Locatable {
	public static final Tile NIL = new Tile(-1, -1, -1);
	public static final Color TARGET_COLOR = new Color(255, 0, 0, 75);
	public final int x;
	public final int y;
	public final int plane;

	public Tile(final int x, final int y) {
		this(x, y, 0);
	}

	public Tile(final int x, final int y, final int plane) {
		this.x = x;
		this.y = y;
		this.plane = plane;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getPlane() {
		return plane;
	}

	/**
	 * Retrieves the {@link TileMatrix} for this {@link Tile}.
	 *
	 * @param ctx The context to retrieve the matrix in.
	 * @return the {@link TileMatrix} of this {@link Tile} with the given context
	 */
	public TileMatrix getMatrix(MethodContext ctx) {
		return new TileMatrix(ctx, this);
	}

	public Tile derive(final int x, final int y) {
		return derive(x, y, this.plane);
	}

	public Tile derive(final int x, final int y, final int plane) {
		return new Tile(this.x + x, this.y + y, plane);
	}

	public Tile randomize(final int left, final int right, final int down, final int up) {
		return derive(Random.nextInt(left, right + 1), Random.nextInt(down, up + 1));
	}

	public Tile randomize(final int x, final int y) {
		return randomize(-x, x, -y, y);
	}

	public double distanceTo(final Locatable l) {
		Tile t = l != null ? l.getLocation() : null;
		if (t == null || plane != t.plane) return Double.POSITIVE_INFINITY;
		final int dx = x - t.x, dy = y - t.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	@Override
	public int hashCode() {
		return x * 31 + y;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + plane + ')';
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Tile)) {
			return false;
		}
		final Tile t = (Tile) o;
		return x == t.x && y == t.y && plane == t.plane;
	}

	@Override
	public Tile getLocation() {
		return this;
	}
}
