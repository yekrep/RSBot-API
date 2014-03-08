package org.powerbot.script.os;

import org.powerbot.script.Random;

public class Tile implements Locatable {
	public static final Tile NIL = new Tile(-1, -1, -1);
	private final int x;
	private final int y;
	private final int floor;

	public Tile(final int x, final int y) {
		this(x, y, 0);
	}

	public Tile(final int x, final int y, final int floor) {
		this.x = x;
		this.y = y;
		this.floor = floor;
	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}

	public int floor() {
		return floor;
	}

	/**
	 * Retrieves the {@link TileMatrix} for this {@link Tile}.
	 *
	 * @param ctx The context to retrieve the matrix in.
	 * @return the {@link TileMatrix} of this {@link Tile} with the given context
	 */
	public TileMatrix matrix(final ClientContext ctx) {
		return new TileMatrix(ctx, this);
	}

	public Tile derive(final int x, final int y) {
		return derive(x, y, floor);
	}

	public Tile derive(final int x, final int y, final int plane) {
		if (this == NIL) {
			return NIL;
		}
		return new Tile(this.x + x, this.y + y, plane);
	}

	public Tile randomize(final int left, final int right, final int down, final int up) {
		return derive(Random.nextInt(left, right + 1), Random.nextInt(down, up + 1));
	}

	public Tile randomize(final int x, final int y) {
		return randomize(-x, x, -y, y);
	}

	public double distanceTo(final Locatable l) {
		final Tile t = l != null ? l.tile() : null;
		if (t == null || floor != t.floor || floor == NIL.floor) {
			return Double.POSITIVE_INFINITY;
		}
		final int dx = x - t.x, dy = y - t.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	@Override
	public int hashCode() {
		return x * 31 + y;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + floor + ')';
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Tile)) {
			return false;
		}
		final Tile t = (Tile) o;
		return x == t.x && y == t.y && floor == t.floor;
	}

	@Override
	public Tile tile() {
		return this;
	}
}
